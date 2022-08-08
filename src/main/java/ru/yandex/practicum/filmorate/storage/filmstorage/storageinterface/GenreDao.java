package ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;

public interface GenreDao {
    Genre getGenreFromDb(int genreId);

    Collection<Genre> getAllGenres();

  //  Set<Genre> getGenresFromDb(Film film);

    void addFilmsGenres(Film film);

    void updateFilmsGenres(Film film);
}
