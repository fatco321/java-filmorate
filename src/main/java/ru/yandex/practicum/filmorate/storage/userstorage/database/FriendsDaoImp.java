package ru.yandex.practicum.filmorate.storage.userstorage.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserFriendException;
import ru.yandex.practicum.filmorate.storage.userstorage.storageinterface.FriendsDao;

import java.util.List;

@Component
@Slf4j
public class FriendsDaoImp implements FriendsDao {
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public FriendsDaoImp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public List<Long> getFriends(long userId) {
        if (noExists(userId)) {
            log.debug("user id {}", userId);
            throw new IdNotFoundException(String.format("User with id %s not found", userId));
        }
        String sql = "select friend_id from friends where user_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, userId);
    }
    
    @Override
    public void addFriend(long userId, long friendUserId) {
        checkEqualityIdAndExists(userId, friendUserId);
        String sql = "insert into friends (user_id, friend_id, friend_status) values (? ,? , false)";
        jdbcTemplate.update(sql, userId, friendUserId);
        checkAndChangeFriendsStatus(userId, friendUserId);
        log.debug("user {} friend with user {}", userId, friendUserId);
    }
    
    @Override
    public void deleteFriend(long userId, long friendUserId) {
        checkEqualityIdAndExists(userId, friendUserId);
        String sql = "delete from friends where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sql, userId, friendUserId);
        log.debug("user {} delete friend {}", userId, friendUserId);
    }
    
    private void checkEqualityIdAndExists(long userId, long friendUserId) {
        if (userId == friendUserId) {
            log.debug("user {} friend id {}", userId, friendUserId);
            throw new UserFriendException(String.format("user id %s = friends id %s", userId, friendUserId));
        }
        if (noExists(userId) || noExists(friendUserId)) {
            log.debug("User id {} friend id {}", userId, friendUserId);
            throw new IdNotFoundException(String.format("Users with id %s, %s not found", userId, friendUserId));
        }
    }
    
    private void checkAndChangeFriendsStatus(long userId, long friendUserId) {
        String sql = "select count(*) from friends where friend_id = ? and user_id = ?";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendUserId);
        if (result == 1) {
            sql = "update friends set friend_status = true where user_id = ? and friend_id = ?";
            jdbcTemplate.update(sql, userId, friendUserId);
            jdbcTemplate.update(sql, friendUserId, userId);
        }
    }
    
    @Override
    public List<Long> getUserAllFriendsId(long userId) {
        if (noExists(userId)) {
            log.debug("user id {}", userId);
            throw new IdNotFoundException(String.format("User with id %s not found", userId));
        }
        String sql = "select friend_id from friends where user_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, userId);
    }
    
    private boolean noExists(long id) {
        String sql = "select count(*) from users where user_id = ?";
        int result = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return result == 0;
    }
}
