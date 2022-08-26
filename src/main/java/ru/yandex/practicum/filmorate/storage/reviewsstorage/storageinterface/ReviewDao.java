package ru.yandex.practicum.filmorate.storage.reviewsstorage.storageinterface;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewDao {
    Review createReview(Review review);

    void updateReview(Review review);

    void deleteReview(long reviewId);

    Optional<Review> getReviewById(long id);

    Optional<Review> getReviewByUserAndFilm(long userId, long filmId);

    List<Review> getListReviews(long filmId, int count);
}
