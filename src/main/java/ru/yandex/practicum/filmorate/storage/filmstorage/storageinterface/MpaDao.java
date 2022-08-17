package ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaDao {
    Collection<Mpa> getAllMpa();
    
    Mpa getMpaFromDb(int mpaId);
}
