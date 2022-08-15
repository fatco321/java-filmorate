package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.GenreDao;

import java.util.Collection;

@Service
public class GenreService {
    private final GenreDao genreDao;

    @Autowired
    public GenreService(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Collection<Genre> getAllGenres() {
        return genreDao.getAllGenres();
    }

    public Genre getGenre(int genreId) {
        return genreDao.getGenreFromDb(genreId);
    }
}
