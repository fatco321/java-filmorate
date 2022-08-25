package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmLikeDao;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmStorage;
import ru.yandex.practicum.filmorate.storage.userstorage.storageinterface.UserRecommendations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис поиска рекомендаций для пользователя.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationsService {
    private final UserRecommendations userRecommendations;
    private final FilmLikeDao filmLikeDao;
    private final FilmStorage filmStorage;

    /**
     * Возвращает список рекомендованных фильмов.
     *
     * @param userId id пользователя.
     */
    public List<Film> getRecommendations(Long userId) {
        log.debug("RRequest for recommendation for User ID {} in processing.", userId);
        List<Film> recommendedFilms = new ArrayList<>();
        Set<Long> userFilmIds = filmLikeDao.getUserFilmLikes(userId);
        if (userFilmIds.size() == 0) {
            return recommendedFilms;
        }

        Long likeMindedUserId = userRecommendations.getLikeMindedUserId(userId);
        if (likeMindedUserId == null) {
            return recommendedFilms;
        }

        Set<Long> recommendedFilmIds = filmLikeDao.getUserFilmLikes(likeMindedUserId);
        recommendedFilmIds.removeAll(userFilmIds);
        recommendedFilms = recommendedFilmIds.stream()
                .map(filmStorage::findFilmById)
                .collect(Collectors.toList());

        return recommendedFilms;
    }
}
