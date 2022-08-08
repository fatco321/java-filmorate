package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.MpaDao;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
public class MpaRatingController {
    private final MpaDao mpaDao;

    @Autowired
    public MpaRatingController(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    @GetMapping
    public Collection<Mpa> getAllMpaRatings() {
        return mpaDao.getAllMpa();
    }

    @GetMapping("/{MpaId}")
    public Mpa getMpaRating(@PathVariable("MpaId") int mpaId) {
        return mpaDao.getMpaFromDb(mpaId);
    }
}
