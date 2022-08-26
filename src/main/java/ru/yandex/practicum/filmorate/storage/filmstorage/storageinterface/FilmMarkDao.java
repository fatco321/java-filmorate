package ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface;

import java.util.Set;

public interface FilmMarkDao {
    boolean isExistFilmMarkById(long filmId, long userId);
    void addMark(long filmId, long userId, int mark);
    public void deleteMark(long filmId, long userId);
    double getFilmMarkAvgById(long filmId);
    Set<Long> getUserFilmsWithPositiveMark(long userId);
}
