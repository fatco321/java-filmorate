package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyUseException;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.service.serviseinterface.FilmService;
import ru.yandex.practicum.filmorate.storage.feedstorage.storageinterface.FeedDao;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmLikeDao;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmMarkDao;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmStorage;

import java.util.List;

@Service("DbFilmService")
@Getter
@Slf4j
@RequiredArgsConstructor
public class FilmServiceDb implements FilmService {
    private final FilmStorage filmStorage;
    private final FilmLikeDao filmLikeDao;
    private final FeedDao feedDao;
    private final FilmMarkDao filmMarkDao;


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
        feedDao.createFeed(userId, filmId, EventType.LIKE, Operation.ADD);
    }

    @Override
    public void deleteFilmLike(long filmId, long userId) {
        checkId(filmId, userId);
        filmLikeDao.deleteLike(filmId, userId);
        feedDao.createFeed(userId, filmId, EventType.LIKE, Operation.REMOVE);
    }

    @Override
    public List<Film> getPopularFilms(int count, int genreId, int year, boolean mark) {
        return mark ?
                filmStorage.getPopularFilmsByMarksAvg(count, genreId, year)
                : filmStorage.getPopularFilms(count, genreId, year);
    }

    @Override
    public List<Film> getDirectorFilms(long directorId, String sortBy) {
        if ("year".equalsIgnoreCase(sortBy) || "likes".equalsIgnoreCase(sortBy) || "marks".equalsIgnoreCase(sortBy)) {
            return filmStorage.getDirectorFilms(directorId, sortBy);
        } else {
            throw new BadRequestException(String.format("RequestParam sortBy = %s is invalid. " +
                    "Must be \"likes\" or \"year\" or \"marks\"", sortBy));
        }
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        boolean title = by.toLowerCase().contains("title");
        boolean director = by.toLowerCase().contains("director");
        return filmStorage.searchFilms(query, title, director);
    }

    @Override
    public void addFilmMark(long filmId, long userId, int mark) {
        checkId(filmId, userId);
        checkMark(mark);
        log.debug("User {} give mark {} to film {}", userId, mark, filmId);
        isExistFilmMarkById(filmId, userId);
        filmMarkDao.addMark(filmId, userId, mark);
        feedDao.createFeed(userId, filmId, EventType.MARK, Operation.ADD);
    }

    @Override
    public void deleteFilmMark(long filmId, long userId) {
        checkId(filmId, userId);
        filmMarkDao.deleteMark(filmId, userId);
        feedDao.createFeed(userId, filmId, EventType.MARK, Operation.REMOVE);
    }

    private void checkMark(int mark) {
        if (mark < 0 || mark > 10) {
            throw new BadRequestException(
                    String.format("Оценка должна быть больше 0, но меньше 10. Ваша оценка = %d", mark));
        }
    }

    private void isExistFilmMarkById(long filmId, long userId) {
        if (filmMarkDao.isExistFilmMarkById(filmId, userId)) {
            throw new AlreadyUseException(String.format("user {} уже ставил оценку фильму {}", userId, filmId));
        }
    }
}
