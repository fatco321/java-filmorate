package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserFriendException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userstorage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Getter
public class UserService {
    private final InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.userStorage = inMemoryUserStorage;
    }

    public void addFriend(long userId, long friendUserId) {
        checkUserAndFriendId(userId, friendUserId);
        if (userStorage.findUserById(userId).getFriendsId().contains(userStorage.findUserById(friendUserId).getId())) {
            throw new UserFriendException(String.format(
                    "User %s already friends with %s", userStorage.findUserById(userId).getName(),
                    userStorage.findUserById(friendUserId).getName()));
        }
        if (userId == friendUserId) {
            throw new UserFriendException("You cannot be friend for you");
        }
        userStorage.findUserById(userId).getFriendsId().add(friendUserId);
        userStorage.findUserById(friendUserId).getFriendsId().add(userId);
    }

    public List<User> getFriendList(long userId) {
        if (userId <= 0) {
            throw new IdNotFoundException(String.format("user with id:%s not found", userId));
        }
        List<User> friendList = new ArrayList<>();
        for (long friendId : userStorage.findUserById(userId).getFriendsId()) {
            friendList.add(userStorage.findUserById(friendId));
        }
        return friendList;
    }

    public void deleteFriend(long userId, long friendUserId) {
        checkUserAndFriendId(userId, friendUserId);
        if (!userStorage.findUserById(userId).getFriendsId().contains(userStorage.findUserById(friendUserId).getId())) {
            throw new UserFriendException(String.format(
                    "You not friends with %s", userStorage.findUserById(friendUserId).getName()
            ));
        }
        if (userId == friendUserId) {
            throw new UserFriendException("You cannot delete yourself from friends");
        }
        userStorage.findUserById(userId).getFriendsId().remove(friendUserId);
        userStorage.findUserById(friendUserId).getFriendsId().remove(userId);
    }

    public List<User> getListMutualFriends(long userId, long friendUserId) {
        checkUserAndFriendId(userId, friendUserId);
        List<Long> mutualFriendsId = userStorage.findUserById(userId).getFriendsId().stream()
                .filter(userStorage.findUserById(friendUserId).getFriendsId()::contains)
                .collect(toList());
        List<User> mutualFriends = new ArrayList<>();
        for (long mutualId : mutualFriendsId) {
            mutualFriends.add(userStorage.findUserById(mutualId));
        }
        return mutualFriends;
    }

    private void checkUserAndFriendId(Long userId, Long friendId) {
        if (userId == null || friendId == null || userId <= 0 || friendId <= 0) {
            throw new IdNotFoundException(String.format("User with id:%s or user friend with id:%s not found",
                    userId, friendId));
        }
    }
}
