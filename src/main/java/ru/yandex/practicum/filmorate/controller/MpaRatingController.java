package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
public class MpaRatingController {
    private final MpaService mpaService;

    @Autowired
    public MpaRatingController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public Collection<Mpa> getAllMpaRatings() {
        return mpaService.getAllMpaRatings();
    }

    @GetMapping("/{MpaId}")
    public Mpa getMpaRating(@PathVariable("MpaId") int mpaId) {
        return mpaService.getMpaRating(mpaId);
    }
}
