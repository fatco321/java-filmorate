package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController()
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> filmMap = new HashMap<>();
    private Integer id = 1;
    private static final LocalDate FIRST_FILM_RELEASE = LocalDate.of(1895, 12, 28);

    private Integer setId() {
        return id++;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if (filmMap.containsKey(film.getId())) {
            log.debug("Film id:{}", film.getId());
            throw new IdException("Id already use");
        }
        film.setId(setId());
        validate(film);
        filmMap.put(film.getId(), film);
        log.info("film with id:{} create", film.getId());
        return film;
    }

    public void validate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE)) {
            log.debug("film not valid release date:{}", film.getReleaseDate());
            throw new ValidationException("Release date not valid");
        }
    }

    @GetMapping
    public Collection<Film> getAll() {
        return filmMap.values();
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        if (!filmMap.containsKey(film.getId())) {
            log.debug("Film id:{}", film.getId());
            throw new IdException("Id not found");
        }
        validate(film);
        filmMap.put(film.getId(), film);
        log.info("Film with id:{} update", film.getId());
        return film;
    }

}
