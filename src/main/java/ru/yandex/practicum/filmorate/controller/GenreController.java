package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.GenreDao;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreDao genreDao;

    @Autowired
    public GenreController(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    @GetMapping
    public Collection<Genre> getAllGenre() {
        return genreDao.getAllGenres();
    }

    @GetMapping("/{genreId}")
    public Genre getGenre(@PathVariable("genreId") int genreId) {
        return genreDao.getGenreFromDb(genreId);
    }
}
