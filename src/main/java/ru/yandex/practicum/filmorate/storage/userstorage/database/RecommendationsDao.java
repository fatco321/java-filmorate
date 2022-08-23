package ru.yandex.practicum.filmorate.storage.userstorage.database;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.userstorage.storageinterface.UserRecommendations;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO для сервиса рекомендаций.
 */
@Component
@RequiredArgsConstructor
public class RecommendationsDao implements UserRecommendations {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long getLikeMindedUserId(Long userId) {
        String sql = "select UIM.USER_ID as ID, MAX(MATCHES), (TotalLikes - MATCHES) diff " +
                "from (select USER_ID, count(USER_ID) MATCHES " +
                    "from FILMS_LIKES " +
                    "where FILM_ID in (select FILM_ID from FILMS_LIKES where USER_ID = ?) and USER_ID != ?" +
                    "group by USER_ID " +
                    "order by MATCHES desc) UIM " +
                        "left join (select USER_ID, count(FILM_ID) TotalLikes " +
                        "from FILMS_LIKES " +
                        "group by USER_ID) as LikesCount " +
                        "on UIM.USER_ID = LikesCount.USER_ID " +
                "where TotalLikes - MATCHES > 0 " +
                "group by UIM.USER_ID " +
                "order by diff desc " +
                "limit 1;";
            return jdbcTemplate.query(sql, this::mapRowToInteger, userId, userId).stream().findAny().orElse(null);
    }

    private Long mapRowToInteger(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("ID");
    }
}
