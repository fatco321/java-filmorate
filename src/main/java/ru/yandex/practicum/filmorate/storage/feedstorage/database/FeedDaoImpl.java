package ru.yandex.practicum.filmorate.storage.feedstorage.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.feedstorage.storageinterface.FeedDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FeedDaoImpl implements FeedDao {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public Feed createFeed(Feed feed) {
        
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("FEEDS")
            .usingGeneratedKeyColumns("EVENT_ID");
        long savedId = simpleJdbcInsert.executeAndReturnKey(feed.toMap()).longValue();
        feed.setEventId(savedId);
        log.info("Создано событие {}", feed);
        return feed;
    }
    
    @Override
    public List<Feed> getUserFeeds(long userId) {
        
        String sql = "SELECT * FROM feeds " +
            "WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFeed(rs), userId);
    }
    
    private Feed makeFeed(ResultSet resultSet) throws SQLException {
        return Feed.builder()
            .timestamp(resultSet.getLong("TIMESTAMP"))
            .userId(resultSet.getLong("USER_ID"))
            .eventType(EventType.valueOf(resultSet.getString("EVENT_TYPE")))
            .operation(Operation.valueOf(resultSet.getString("OPERATION")))
            .eventId(resultSet.getLong("EVENT_ID"))
            .entityId(resultSet.getLong("ENTITY_ID")).build();
    }
}