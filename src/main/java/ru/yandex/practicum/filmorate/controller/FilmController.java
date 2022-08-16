package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.serviseinterface.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController()
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;
    
    public FilmController(@Qualifier("DbFilmService") FilmService filmService) {
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
    
    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilms(@PathVariable long directorId, @RequestParam String sortBy) {
        if ("year".equalsIgnoreCase(sortBy) || "likes".equalsIgnoreCase(sortBy)) {
            return filmService.getFilmStorage().getDirectorFilms(directorId, sortBy);
        } else {
            throw new BadRequestException(String.format("RequestParam sortBy = %s is invalid. Must be \"likes\" or \"year\"", sortBy));
        }
    }
}
