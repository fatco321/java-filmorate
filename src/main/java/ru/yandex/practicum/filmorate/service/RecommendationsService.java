package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmLikeDao;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmMarkDao;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmStorage;
import ru.yandex.practicum.filmorate.storage.userstorage.storageinterface.UserRecommendations;

import java.util.*;
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
    private final FilmMarkDao filmMarkDao;
    private Map<Long, Map<Long, Integer>> data = new HashMap<>();

    /**
     * Возвращает список рекомендованных фильмов.
     *
     * @param userId id пользователя.
     */
    public List<Film> getRecommendations(Long userId, boolean mark) {
            return mark ? getRecommendationsByMarkAvg(userId) : getRecommendationsByLikes(userId);
    }

    private List<Film> getRecommendationsByLikes (Long userId){
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

    private List<Film> getRecommendationsByMarkAvg(Long userId) {
        log.debug("Request for recommendation for User ID {} by mark processing.", userId);

        fillData(userId);
        Set<Long> usersId = userRecommendations.getOtherUserIdWhichMarkedAtTheSameFilms(userId);
        for (Long id : usersId) {
            fillData(id);
        }
        Map<Long, Integer> userFilmsMap = data.get(userId);
        data.remove(userId);
        long coincidenceUserId = 0;
        int coincidence = 0;

        for (Map.Entry<Long, Integer> userFilmsEntry : userFilmsMap.entrySet()) {
            Long userFilmId = userFilmsEntry.getKey();
            Integer userMark = userFilmsEntry.getValue();
            for (Map.Entry<Long, Map<Long, Integer>> usersMap : data.entrySet()) {
                long otherUserId = usersMap.getKey();
                int identity = 0;
                for (Map.Entry<Long, Integer> usersEntry : usersMap.getValue().entrySet()) {
                    long otherFilmId = usersEntry.getKey();
                    int otherUserMark = usersEntry.getValue();
                    if (userFilmId == otherFilmId && userMark == otherUserMark) {
                        identity++;
                    }
                }
                if (identity > coincidence) {
                    coincidence = identity;
                    coincidenceUserId = otherUserId;
                }
            }
        }

        Set<Long> userFilmWithPositiveMark = filmMarkDao.getUserFilmsWithPositiveMark(userId);
        Set<Long> otherUserFilmWithPositiveMark = filmMarkDao.getUserFilmsWithPositiveMark(coincidenceUserId);
        otherUserFilmWithPositiveMark.removeAll(userFilmWithPositiveMark);
        return otherUserFilmWithPositiveMark.stream()
                .map(filmStorage :: findFilmById)
                .collect(Collectors.toList());

    }

    private void fillData(long userId) {
        List<Map<Long, Integer>> list = userRecommendations.getUserListFilmsWithMarks(userId);
        Map<Long, Integer> map = new HashMap<>();
        for (Map<Long, Integer> pair : list) {
            map.putAll(pair);
        }
        data.put(userId, map);
    }
}
