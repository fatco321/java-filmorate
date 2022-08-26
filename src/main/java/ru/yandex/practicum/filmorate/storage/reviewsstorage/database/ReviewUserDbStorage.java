package ru.yandex.practicum.filmorate.storage.reviewsstorage.database;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.reviewsstorage.storageinterface.ReviewUserDao;

@Repository
@AllArgsConstructor
public class ReviewUserDbStorage implements ReviewUserDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLikeToReview(long reviewId, long userId) {
        String sql = "insert into REVIEWS_USERS (REVIEW_ID, USER_ID, IS_LIKE) " +
                "values (?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, true);
    }

    @Override
    public void addDislikeToReview(long reviewId, long userId) {
        String sql = "insert into REVIEWS_USERS (REVIEW_ID, USER_ID, IS_LIKE) " +
                "values (?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, false);
    }

    @Override
    public void deleteLikeToReview(long reviewId, long userId) {
        String sql = "delete from REVIEWS_USERS " +
                "where REVIEW_ID = ? and USER_ID = ? and IS_LIKE = TRUE";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    @Override
    public void deleteDislikeToReview(long reviewId, long userId) {
        String sql = "delete from REVIEWS_USERS " +
                "where REVIEW_ID = ? and USER_ID = ? and IS_LIKE = FALSE";
        jdbcTemplate.update(sql, reviewId, userId);
    }

}