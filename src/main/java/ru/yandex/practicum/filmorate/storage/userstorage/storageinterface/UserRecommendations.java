package ru.yandex.practicum.filmorate.storage.userstorage.storageinterface;

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
}
