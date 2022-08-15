package ru.yandex.practicum.filmorate.storage.userstorage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.userstorage.storageinterface.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Slf4j
@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> userMap = new HashMap<>();
    private int id = 1;

    private int setId() {
        return id++;
    }

    @Override
    public User addUser(User user) {
        if (userMap.containsKey(user.getId())) {
            log.debug("User with id:{}, already exist", user.getId());
            throw new IdNotFoundException("Id already use");
        }
        user.setId(setId());
        validate(user);
        user.setFriendsId(new HashSet<>());
        userMap.put(user.getId(), user);
        log.info("User with id:{} create", user.getId());
        return user;
    }

    @Override
    public void deleteUser(long userId) {
        if (!userMap.containsKey(userId)) {
            log.debug("User id:{}", userId);
            throw new IdNotFoundException("Id not found");
        }
        userMap.remove(userId);
    }

    @Override
    public User updateUser(User user) {
        if (!userMap.containsKey(user.getId())) {
            log.debug("User id:{}", user.getId());
            throw new IdNotFoundException("Id not found");
        }
        if (user.getFriendsId() == null) {
            user.setFriendsId(userMap.get(user.getId()).getFriendsId());
        }
        validate(user);
        userMap.put(user.getId(), user);
        log.info("User with id:{} update", user.getId());
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        return userMap.values();
    }

    @Override
    public void deleteAllUsers() {
        userMap.clear();
    }

    @Override
    public User findUserById(long id) {
        if (!userMap.containsKey(id)) {
            throw new IdNotFoundException("User not found");
        }
        return userMap.get(id);
    }

    private void validate(User user) {
        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
            log.debug("User id:{}, with empty name", user.getId());
        }
    }
}
