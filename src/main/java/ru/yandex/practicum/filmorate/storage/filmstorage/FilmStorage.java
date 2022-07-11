package ru.yandex.practicum.filmorate.storage.filmstorage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film addFilm(Film film);

    void deleteFilm(long flmId);

    Film updateFilm(Film film);

    Collection<Film> getAllFilms();

    void deleteAllFilms();

    Film findFilmById(long filmId);
}
