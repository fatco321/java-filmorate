package ru.yandex.practicum.filmorate.service.serviseinterface;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmStorage;

import java.util.List;

public interface FilmService {
    void addFilmLike(long filmId, long userId);

    void deleteFilmLike(long filmId, long userId);

    List<Film> getPopularFilms(int count);

    FilmStorage getFilmStorage();

    List<Film> getDirectorFilms(long directorId, String sortBy);

    List<Film> getCommonFilms(long userId, long friendId);
}
