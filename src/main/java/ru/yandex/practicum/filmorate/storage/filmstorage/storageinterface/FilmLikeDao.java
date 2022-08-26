package ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface;

import java.util.Set;

public interface FilmLikeDao {
    Set<Long> getUserLikes(long filmId);
    
    void addLike(long filmId, long userId);
    
    void deleteLike(long filmId, long userId);

    /**
     * Получение сета всех фильмов, которым пользователь поставил лайк.
     * @param userId id пользователя.
     */
    Set<Long> getUserFilmLikes(long userId);

}
