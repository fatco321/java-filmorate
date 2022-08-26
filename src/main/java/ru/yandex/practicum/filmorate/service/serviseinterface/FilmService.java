package ru.yandex.practicum.filmorate.service.serviseinterface;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmStorage;

import java.util.List;

public interface FilmService {
    void addFilmLike(long filmId, long userId);

    void deleteFilmLike(long filmId, long userId);

    List<Film> getPopularFilms(int count, int genreId, int year, boolean mark);

    FilmStorage getFilmStorage();

    List<Film> getDirectorFilms(long directorId, String sortBy);

    List<Film> getCommonFilms(long userId, long friendId);

    List<Film> searchFilms(String query, String by);

    void addFilmMark(long filmId, long userId, int mark);
    void deleteFilmMark(long filmId, long userId);
}
