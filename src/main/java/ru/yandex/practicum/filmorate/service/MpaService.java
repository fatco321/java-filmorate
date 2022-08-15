package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.MpaDao;

import java.util.Collection;

@Service
public class MpaService {
    private final MpaDao mpaDao;

    @Autowired
    public MpaService(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    public Mpa getMpaRating(int mpaId) {
        return mpaDao.getMpaFromDb(mpaId);
    }

    public Collection<Mpa> getAllMpaRatings() {
        return mpaDao.getAllMpa();
    }
}
