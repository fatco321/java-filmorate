package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.serviseinterface.ReviewService;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmStorage;
import ru.yandex.practicum.filmorate.storage.reviewsstorage.storageinterface.ReviewDao;
import ru.yandex.practicum.filmorate.storage.reviewsstorage.storageinterface.ReviewUserDao;
import ru.yandex.practicum.filmorate.storage.userstorage.storageinterface.UserStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewsServiceDB implements ReviewService {

    private final ReviewDao reviewDao;
    private final ReviewUserDao reviewUserDao;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Override
    public Review addReview(Review review) {
        userStorage.findUserById(review.getUserId());
        filmStorage.findFilmById(review.getFilmId());
        review.setUseful(0);
        return reviewDao.createReview(review);
    }

    @Override
    public Review updateReview(Review review) {
        reviewDao.getReviewById(review.getReviewId());
        reviewDao.updateReviewContent(review);
        return review;
    }

    @Override
    public void deleteReview(long id) {
        reviewDao.deleteReview(id);
    }

    @Override
    public Review getReviewById(long id) {
        return reviewDao.getReviewById(id);
    }

    @Override
    public List<Review> getListReview(long filmId, int count) {

        return reviewDao.getListReviews(filmId, count);
    }

    @Override
    public void addLikeToReview(long reviewId, long userId) {
        Review review = reviewDao.getReviewById(reviewId);
        review.setUseful(review.getUseful() + 1);
        reviewDao.updateReviewUseful(review);
        reviewUserDao.addLikeToReview(reviewId, userId);
    }

    @Override
    public void addDislikeToReview(long reviewId, long userId) {
        Review review = reviewDao.getReviewById(reviewId);
        review.setUseful(review.getUseful() - 1);
        reviewDao.updateReviewUseful(review);
        reviewUserDao.addDislikeToReview(reviewId, userId);
    }

    @Override
    public void deleteLikeToReview(long reviewId, long userId) {
        Review review = reviewDao.getReviewById(reviewId);
        review.setUseful(review.getUseful() - 1);
        reviewDao.updateReviewUseful(review);
        reviewUserDao.deleteLikeToReview(reviewId, userId);
    }

    @Override
    public void deleteDislikeToReview(long reviewId, long userId) {
        Review review = reviewDao.getReviewById(reviewId);
        review.setUseful(review.getUseful() + 1);
        reviewDao.updateReviewUseful(review);
        reviewUserDao.deleteDislikeToReview(reviewId, userId);
    }

}
