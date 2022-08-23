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
        String sql = "select FRIEND_ID from FRIENDS where USER_ID = ?";
        return jdbcTemplate.queryForList(sql, Long.class, userId);
    }
    
    @Override
    public void addFriend(long userId, long friendUserId) {
        checkEqualityIdAndExists(userId, friendUserId);
        String sql = "insert into FRIENDS (USER_ID, FRIEND_ID, FRIEND_STATUS) values (? ,? , false)";
        jdbcTemplate.update(sql, userId, friendUserId);
        checkAndChangeFriendsStatus(userId, friendUserId);
        log.debug("user {} friend with user {}", userId, friendUserId);
    }
    
    @Override
    public void deleteFriend(long userId, long friendUserId) {
        checkEqualityIdAndExists(userId, friendUserId);
        String sql = "delete from FRIENDS where USER_ID = ? and FRIEND_ID = ?";
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
        String sql = "select count(*) from FRIENDS where FRIEND_ID = ? and USER_ID = ?";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendUserId);
        if (result == 1) {
            sql = "update FRIENDS set FRIEND_STATUS = true where USER_ID = ? and FRIEND_ID = ?";
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
        String sql = "select FRIEND_ID from FRIENDS where USER_ID = ?";
        return jdbcTemplate.queryForList(sql, Long.class, userId);
    }
    
    private boolean noExists(long id) {
        String sql = "select count(*) from USERS where USER_ID = ?";
        int result = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return result == 0;
    }
}
