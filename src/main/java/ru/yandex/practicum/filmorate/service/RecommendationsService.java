package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmLikeDao;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmStorage;
import ru.yandex.practicum.filmorate.storage.userstorage.database.RecommendationsDao;
import ru.yandex.practicum.filmorate.storage.userstorage.storageinterface.UserRecommendations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Сервис поиска рекомендаций для пользователя.
 */
@Service
public class RecommendationsService {
    private final UserRecommendations userRecommendations;
    private final FilmLikeDao filmLikeDao;
    private final FilmStorage filmStorage;

    @Autowired
    public RecommendationsService(RecommendationsDao userRecommendations, FilmLikeDao filmLikeDao,
                                  FilmStorage filmStorage) {
        this.userRecommendations = userRecommendations;
        this.filmLikeDao = filmLikeDao;
        this.filmStorage = filmStorage;
    }

    /**
     * Возвращает список рекомендованных фильмов.
     *
     * @param userId id пользователя.
     */
    public List<Film> getRecommendations(Long userId) {
        List<Film> recommendedFilms = new ArrayList<>();
        Set<Long> userFilms = filmLikeDao.getUserFilmLikes(userId);

       if (userFilms.size() == 0) {
            return recommendedFilms;
        }

        Long likeMindedUserId = userRecommendations.getLikeMindedUserId(userId);
        if (likeMindedUserId == null) {
            return recommendedFilms;
        }

        Set<Long> recommendedFilmIds = filmLikeDao.getUserFilmLikes(likeMindedUserId);
        recommendedFilmIds.removeAll(userFilms);

        for (Long id : recommendedFilmIds) {
            recommendedFilms.add(filmStorage.findFilmById(id));
        }

        return recommendedFilms;
    }
}
