package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.service.serviseinterface.FilmService;
import ru.yandex.practicum.filmorate.storage.feedstorage.storageinterface.FeedDao;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmLikeDao;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmStorage;

import java.time.Instant;
import java.util.List;

@Service("DbFilmService")
@Getter
@Slf4j
@RequiredArgsConstructor
public class FilmServiceDb implements FilmService {
    private final FilmStorage filmStorage;
    private final FilmLikeDao filmLikeDao;
    private final FeedDao feedDao;
    
    
    private void checkId(Long filmId, Long userId) {
        log.debug("check user {} check film {}", userId, filmId);
        if (filmId == null || filmId <= 0 || userId == null || userId <= 0) {
            throw new IdNotFoundException(String.format("User with id:%s or film with id:%s not found",
                userId, filmId));
        }
    }
    
    @Override
    public void addFilmLike(long filmId, long userId) {
        checkId(filmId, userId);
        log.debug("User {} likes film {}", userId, filmId);
        filmLikeDao.addLike(filmId, userId);
        
        feedDao.createFeed(Feed.builder()
            .timestamp(Instant.now().toEpochMilli())
            .userId(userId)
            .entityId(filmId)
            .operation(Operation.ADD)
            .eventType(EventType.LIKE)
            .build()
        );
    }
    
    @Override
    public void deleteFilmLike(long filmId, long userId) {
        checkId(filmId, userId);
        filmLikeDao.deleteLike(filmId, userId);
        
        feedDao.createFeed(Feed.builder()
            .timestamp(Instant.now().toEpochMilli())
            .userId(userId)
            .entityId(filmId)
            .operation(Operation.REMOVE)
            .eventType(EventType.LIKE)
            .build()
        );
    }
    
    @Override
    public List<Film> getPopularFilms(int count, int genreId, int year) {
        return filmStorage.getPopularFilms(count, genreId, year);
    }
    
    @Override
    public List<Film> getDirectorFilms(long directorId, String sortBy) {
        if ("year".equalsIgnoreCase(sortBy) || "likes".equalsIgnoreCase(sortBy)) {
            return filmStorage.getDirectorFilms(directorId, sortBy);
        } else {
            throw new BadRequestException(String.format("RequestParam sortBy = %s is invalid. Must be \"likes\" or \"year\"", sortBy));
        }
    }
    
    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }
}
