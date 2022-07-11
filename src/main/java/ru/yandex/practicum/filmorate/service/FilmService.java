package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmLikeNotFoundException;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.filmstorage.InMemoryFilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Getter
public class FilmService {
    private final InMemoryFilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.filmStorage = inMemoryFilmStorage;
    }

    private void checkId(Long filmId, Long userId) {
        if (filmId == null || filmId <= 0 || userId == null || userId <= 0) {
            throw new IdNotFoundException(String.format("User with id:%s or film with id:%s not found", userId, filmId));
        }
        if (!filmStorage.getAllFilms().contains(filmStorage.findFilmById(filmId))) {
            throw new IdNotFoundException(String.format("Film with id:%s not found", filmId));
        }
    }

    public void addFilmLike(long filmId, long userId) {
        checkId(filmId, userId);
        filmStorage.findFilmById(filmId).getUsersLike().add(userId);
    }

    public void deleteFilmLike(long filmId, long userId) {
        checkId(filmId, userId);
        if (!filmStorage.findFilmById(filmId).getUsersLike().contains(userId)) {
            throw new FilmLikeNotFoundException(String.format("User with id:%s not like film with id:%s"
                    , userId, filmId));
        }
        filmStorage.findFilmById(filmId).getUsersLike().remove(userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((p0, p1) -> p1.getUsersLike().size() - p0.getUsersLike().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
