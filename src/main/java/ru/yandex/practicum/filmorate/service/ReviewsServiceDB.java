package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyUseException;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.service.serviseinterface.ReviewService;
import ru.yandex.practicum.filmorate.storage.feedstorage.storageinterface.FeedDao;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmStorage;
import ru.yandex.practicum.filmorate.storage.reviewsstorage.storageinterface.ReviewDao;
import ru.yandex.practicum.filmorate.storage.reviewsstorage.storageinterface.ReviewUserDao;
import ru.yandex.practicum.filmorate.storage.userstorage.storageinterface.UserStorage;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ReviewsServiceDB implements ReviewService {

    private final ReviewDao reviewDao;
    private final ReviewUserDao reviewUserDao;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FeedDao feedDao;

    @Override
    public Review addReview(Review review) {
        User user = userStorage.findUserById(review.getUserId());
        Film film = filmStorage.findFilmById(review.getFilmId());
        isNotExistReviewByUserAndFilm(user.getId(), film.getId());
        review.setUseful(0);
        Review resultReview = reviewDao.createReview(review);

        feedDao.createFeed(Feed.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(resultReview.getUserId())
                .entityId(resultReview.getReviewId())
                .eventType(EventType.REVIEW)
                .operation(Operation.ADD)
                .build());
        return resultReview;
    }

    @Override
    public Review updateReview(Review review) {
        Review dbReview = getReviewById(review.getReviewId());
        review.setUseful(dbReview.getUseful());
        reviewDao.updateReview(review);

        feedDao.createFeed(Feed.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(dbReview.getUserId())
                .entityId(dbReview.getReviewId())
                .eventType(EventType.REVIEW)
                .operation(Operation.UPDATE)
                .build());
        return review;
    }

    @Override
    public void deleteReview(long id) {
        Review review = getReviewById(id);
        feedDao.createFeed(Feed.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(review.getUserId())
                .entityId(review.getReviewId())
                .eventType(EventType.REVIEW)
                .operation(Operation.REMOVE)
                .build());

        reviewDao.deleteReview(id);
    }

    @Override
    public Review getReviewById(long id) {
        Optional<Review> reviewOptional = reviewDao.getReviewById(id);
        if (reviewOptional.isEmpty()) {
            throw new IdNotFoundException(String.format("Объект с id %d не найден", id));
        }
        return reviewOptional.get();
    }

    @Override
    public List<Review> getListReview(long filmId, int count) {

        return reviewDao.getListReviews(filmId, count);
    }

    @Override
    public void addLikeToReview(long reviewId, long userId) {
        User user = userStorage.findUserById(userId);
        Review review = getReviewById(reviewId);
        review.setUseful(review.getUseful() + 1);
        reviewDao.updateReview(review);
        reviewUserDao.addLikeToReview(reviewId, userId);
    }

    @Override
    public void addDislikeToReview(long reviewId, long userId) {
        User user = userStorage.findUserById(userId);
        Review review = getReviewById(reviewId);
        review.setUseful(review.getUseful() - 1);
        reviewDao.updateReview(review);
        reviewUserDao.addDislikeToReview(reviewId, userId);
    }

    @Override
    public void deleteLikeToReview(long reviewId, long userId) {
        User user = userStorage.findUserById(userId);
        Review review = getReviewById(reviewId);
        review.setUseful(review.getUseful() - 1);
        reviewDao.updateReview(review);
        reviewUserDao.deleteLikeToReview(reviewId, userId);
    }

    @Override
    public void deleteDislikeToReview(long reviewId, long userId) {
        User user = userStorage.findUserById(userId);
        Review review = getReviewById(reviewId);
        review.setUseful(review.getUseful() + 1);
        reviewDao.updateReview(review);
        reviewUserDao.deleteDislikeToReview(reviewId, userId);
    }

    private boolean isNotExistReviewByUserAndFilm(long userId, long filmId) {
        Optional<Review> reviewOptional = reviewDao.getReviewByUserAndFilm(userId, filmId);
        if (reviewOptional.isPresent()) {
            throw new AlreadyUseException(String.format("User c id {} уже создал review на фильм с id {}",
                    userId, filmId));
        }
        return true;
    }

}
