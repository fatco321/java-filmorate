package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.service.serviseinterface.ReviewService;
import ru.yandex.practicum.filmorate.storage.feedstorage.storageinterface.FeedDao;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmStorage;
import ru.yandex.practicum.filmorate.storage.reviewsstorage.storageinterface.ReviewDao;
import ru.yandex.practicum.filmorate.storage.reviewsstorage.storageinterface.ReviewUserDao;
import ru.yandex.practicum.filmorate.storage.userstorage.storageinterface.UserStorage;

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
        userStorage.findUserById(review.getUserId());
        filmStorage.findFilmById(review.getFilmId());
        review.setUseful(0);
        Review resultReview = reviewDao.createReview(review);
        feedDao.createFeed(resultReview.getUserId(),resultReview.getReviewId(), EventType.REVIEW, Operation.ADD);
        return resultReview;
    }

    @Override
    public Review updateReview(Review review) {
        Review dbReview = getReviewById(review.getReviewId());
        review.setUseful(dbReview.getUseful());
        reviewDao.updateReview(review);
        feedDao.createFeed(dbReview.getUserId(),dbReview.getReviewId(), EventType.REVIEW, Operation.UPDATE);
        return review;
    }

    @Override
    public void deleteReview(long id) {
        Review review = getReviewById(id);
       feedDao.createFeed(review.getUserId(),review.getReviewId(), EventType.REVIEW, Operation.REMOVE);
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
        userStorage.findUserById(userId);
        Review review = getReviewById(reviewId);
        review.setUseful(review.getUseful() + 1);
        reviewDao.updateReview(review);
        reviewUserDao.addLikeToReview(reviewId, userId);
    }

    @Override
    public void addDislikeToReview(long reviewId, long userId) {
        userStorage.findUserById(userId);
        Review review = getReviewById(reviewId);
        review.setUseful(review.getUseful() - 1);
        reviewDao.updateReview(review);
        reviewUserDao.addDislikeToReview(reviewId, userId);
    }

    @Override
    public void deleteLikeToReview(long reviewId, long userId) {
        userStorage.findUserById(userId);
        Review review = getReviewById(reviewId);
        review.setUseful(review.getUseful() - 1);
        reviewDao.updateReview(review);
        reviewUserDao.deleteLikeToReview(reviewId, userId);
    }

    @Override
    public void deleteDislikeToReview(long reviewId, long userId) {
        Review review = getReviewById(reviewId);
        review.setUseful(review.getUseful() + 1);
        reviewDao.updateReview(review);
        reviewUserDao.deleteDislikeToReview(reviewId, userId);
    }
}
