package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.serviseinterface.FilmService;
import ru.yandex.practicum.filmorate.storage.filmstorage.database.FilmLikeDaoImp;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmLikeDao;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmStorage;

import java.util.List;

@Service("DbFilmService")
@Getter
@Slf4j
public class FilmServiceDb implements FilmService {
    private final FilmStorage filmStorage;
    private final FilmLikeDao filmLikeDao;

    @Autowired
    public FilmServiceDb(@Qualifier("dbFilmStorage") FilmStorage filmStorage, FilmLikeDaoImp filmLikeDaoImp) {
        this.filmStorage = filmStorage;
        this.filmLikeDao = filmLikeDaoImp;
    }

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
    }

    @Override
    public void deleteFilmLike(long filmId, long userId) {
        checkId(filmId, userId);
        filmLikeDao.deleteLike(filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
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
