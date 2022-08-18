package ru.yandex.practicum.filmorate.storage.reviewsstorage.storageinterface;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDao {
    Review createReview(Review review);

    void updateReviewContent(Review review);

    void updateReviewUseful(Review review);

    void deleteReview(long reviewId);

    Review getReviewById(long id);

    List<Review> getListReviews(long filmId, int count);

}
