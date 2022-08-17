package ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface;

import java.util.Set;

public interface FilmLikeDao {
    Set<Long> getUserLikes(long filmId);
    
    void addLike(long filmId, long userId);
    
    void deleteLike(long filmId, long userId);
}
