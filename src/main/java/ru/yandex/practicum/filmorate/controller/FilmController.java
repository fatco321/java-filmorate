package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController()
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.getFilmStorage().addFilm(film);
    }


    @GetMapping
    public Collection<Film> getAll() {
        return filmService.getFilmStorage().getAllFilms();
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        return filmService.getFilmStorage().updateFilm(film);
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable("filmId") long filmId) {
        return filmService.getFilmStorage().findFilmById(filmId);
    }

    @DeleteMapping
    public void deleteAllFilms() {
        filmService.getFilmStorage().deleteAllFilms();
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable("filmId") long filmId) {
        filmService.getFilmStorage().deleteFilm(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLikeFilm(@PathVariable long filmId, @PathVariable long userId) {
        filmService.addFilmLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLikeFilm(@PathVariable long filmId, @PathVariable long userId) {
        filmService.deleteFilmLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) int count) {
        return filmService.getPopularFilms(count);
    }
}
