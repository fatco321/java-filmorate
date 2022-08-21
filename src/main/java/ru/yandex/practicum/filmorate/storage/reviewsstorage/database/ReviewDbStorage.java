package ru.yandex.practicum.filmorate.storage.reviewsstorage.database;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.reviewsstorage.storageinterface.ReviewDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


@Repository
@AllArgsConstructor
public class ReviewDbStorage implements ReviewDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review createReview(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        long savedId = simpleJdbcInsert.executeAndReturnKey(review.toMap()).longValue();
        review.setReviewId(savedId);
        return review;
    }

    @Override
    public void updateReview(Review review) {
        String sql = "UPDATE REVIEWS " +
                "SET CONTENT = ?, IS_POSITIVE = ?, USEFUL = ? " +
                "WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getUseful(), review.getReviewId());

    }

    @Override
    public void deleteReview(long reviewId) {
        String sql = "DELETE FROM REVIEWS " +
                "WHERE REVIEW_ID= ?";
        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public Optional<Review> getReviewById(long id) {
            String sql = "SELECT * FROM reviews " +
                    "WHERE review_id = ?";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), id).stream().findFirst();
    }

    @Override
    public Optional<Review> getReviewByUserAndFilm(long userId, long filmId){
        String sql = "SELECT * FROM reviews " +
                "WHERE user_id = ? AND film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), userId, filmId).stream().findFirst();
    }

    @Override
    public List<Review> getListReviews(long filmId, int count) {

        if (filmId == 0) {
            String sql = "SELECT * FROM reviews " +
                    "ORDER BY reviews.useful DESC limit ?";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), count);
        }
        String sql = "SELECT * FROM reviews " +
                "WHERE film_id = ?" +
                "ORDER BY reviews.useful DESC limit ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), filmId, count);

    }

    private Review makeReview(ResultSet resultSet) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getInt("review_id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .userId((long) resultSet.getInt("user_id"))
                .filmId((long) resultSet.getInt("film_id"))
                .useful(resultSet.getInt("useful")).build();
    }
}