package ru.yandex.practicum.filmorate.storage.userstorage.database;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.userstorage.storageinterface.UserRecommendations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * DAO для сервиса рекомендаций.
 */
@Component
@RequiredArgsConstructor
public class RecommendationsDao implements UserRecommendations {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long getLikeMindedUserId(Long userId) {
        String sql = "with " +
                "LikesCount as (" +
                "select " +
                "user_id, " +
                "count(film_id) TotalLikes " +
                "from films_likes " +
                "group by user_id), " +
                "uim as (" +
                "select " +
                "user_id, " +
                "count(user_id) matches " +
                "from films_likes " +
                "where film_id in (select film_id from films_likes where user_id = ?) " +
                "and user_id != ? " +
                "group by user_id)" +
                "select " +
                "uim.user_id as id, " +
                "max(matches), " +
                "(TotalLikes - matches) diff " +
                "from uim " +
                "left join LikesCount using(user_id) " +
                "where TotalLikes - matches > 0 " +
                "group by uim.user_id " +
                "order by diff desc " +
                "limit 1;";
        return jdbcTemplate.query(sql, this::mapRowToInteger, userId, userId).stream()
                .findAny()
                .orElse(null);
    }

    private Long mapRowToInteger(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("ID");
    }

    @Override
    public Set<Long> getOtherUserIdWhichMarkedAtTheSameFilms(long userId) {
        String sql = "select l.user_id as userOtherId from films_marks as l " +
                "right join films_marks as r " +
                "where r.user_id = ? and l.film_id = r.film_id";
        List<Long> usersId = jdbcTemplate.queryForList(sql, Long.class, userId);
        return new HashSet<>(usersId);
    }

    @Override
    public List<Map<Long, Integer>> getUserListFilmsWithMarks(long userId) {
        String sql = "select film_id, mark from films_marks " +
                "where user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMap(rs), userId);
    }

    private Map<Long, Integer> makeMap(ResultSet resultSet) throws SQLException {
        Map<Long, Integer> test = new HashMap<>();
        test.put((long) resultSet.getInt("FILM_ID"), resultSet.getInt("MARK"));
        return test;
    }
}
