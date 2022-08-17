package ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    void deleteFilm(long flmId);

    Film updateFilm(Film film);

    Collection<Film> getAllFilms();

    void deleteAllFilms();

    Film findFilmById(long filmId);

    List<Film> getPopularFilms(int count);

    List<Film> getDirectorFilms(long directorId, String sortBy);

    List<Film> getCommonFilms(long userId, long friendId);
}
