package ru.yandex.practicum.filmorate.service.serviseinterface;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmStorage;

import java.util.List;

public interface FilmService {
    void addFilmLike(long filmId, long userId);

    void deleteFilmLike(long filmId, long userId);

    List<Film> getPopularFilms(int count, int genreId, int year);

    FilmStorage getFilmStorage();
}
