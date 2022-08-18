package ru.yandex.practicum.filmorate.storage.reviewsstorage.storageinterface;

public interface ReviewUserDao {
    void addLikeToReview(long reviewId, long userId);

    void addDislikeToReview(long reviewId, long userId);

    void deleteLikeToReview(long reviewId, long userId);

    void deleteDislikeToReview(long reviewId, long userId);

}
