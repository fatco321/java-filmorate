package ru.yandex.practicum.filmorate.storage.userstorage.storageinterface;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Интерфейс получения пользовательских рекомендаций из БД.
 */
public interface UserRecommendations {

    /**
     * Поиск id пользователя, имеющего максимальное пересечение лайков для заданного id.
     * также проверяется наличие лайков фильмов, отсутствующих у заданного пользователя.
     *
     * @param userId id пользователя.
     * @return id максимально подходящего пользователя.
     */
    Long getLikeMindedUserId(Long userId);
    Set<Long> getOtherUserIdWhichMarkedAtTheSameFilms(long userId);
    List<Map<Long, Integer>> getUserListFilmsWithMarks(long userId);
}
