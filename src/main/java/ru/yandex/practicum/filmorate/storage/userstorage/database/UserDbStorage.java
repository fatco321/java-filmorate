package ru.yandex.practicum.filmorate.storage.userstorage.database;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userstorage.storageinterface.FriendsDao;
import ru.yandex.practicum.filmorate.storage.userstorage.storageinterface.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

@Slf4j
@Component("dbUserStorage")
@Getter
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendsDao friendsDao;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, FriendsDao friendsDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.friendsDao = friendsDao;
    }

    @Override
    public User addUser(User user) {
        validate(user);
        user.setId(saveUserAndReturnId(user));
        log.debug("user with id {} create", user.getId());
        return user;
    }

    @Override
    public void deleteUser(long userId) {
        if (noExists(userId)) {
            log.debug("User id:{}", userId);
            throw new IdNotFoundException("Id not found");
        }
        String sql = "DELETE from USERS where USER_ID = ?";
        jdbcTemplate.update(sql, userId);
        log.debug("user with id {} is deleted", userId);
    }

    @Override
    public User updateUser(User user) {
        if (noExists(user.getId())) {
            log.debug("User id:{}", user.getId());
            throw new IdNotFoundException("Id not found");
        }
        String sql = "update USERS set " +
                "USER_NAME = ?, EMAIL = ?, LOGIN = ?, BIRTHDAY = ? where USER_ID = ?";
        jdbcTemplate.update(sql, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());
        log.debug("user with id {} is update", user.getId());
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * from USERS";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public void deleteAllUsers() {
        String sql = "DELETE FROM USERS";
        jdbcTemplate.update(sql);
        log.debug("all users deleted");
    }

    @Override
    public User findUserById(long userId) {
        if (noExists(userId)) {
            log.debug("User id:{}", userId);
            throw new IdNotFoundException("Id not found");
        }
        String sql = "SELECT * from USERS where USER_ID = ?";
        log.debug("getting user with id {}", userId);
        return jdbcTemplate.queryForObject(sql, this::mapRowToUser, userId);
    }

    private void validate(User user) {
        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
            log.debug("User id:{}, with empty name", user.getId());
        }
    }

    private long saveUserAndReturnId(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users").usingGeneratedKeyColumns("user_id");
        return simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .name(resultSet.getString("user_name"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .birthday(LocalDate.parse(resultSet.getString("birthday")))
                .friendsId(new HashSet<>(friendsDao.getFriends(resultSet.getInt("user_id")))).build();
    }

    private boolean noExists(long id) {
        String sql = "select count(*) from USERS where USER_ID = ?";
        int result = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return result == 0;
    }
}
