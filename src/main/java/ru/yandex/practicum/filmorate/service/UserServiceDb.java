package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.serviseinterface.UserService;
import ru.yandex.practicum.filmorate.storage.userstorage.storageinterface.UserStorage;
import ru.yandex.practicum.filmorate.storage.userstorage.storageinterface.FriendsDao;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service("UserServiceDb")
@Slf4j
@Getter
public class UserServiceDb implements UserService {
    private final FriendsDao friendsDao;
    private final UserStorage userStorage;

    @Autowired
    public UserServiceDb(FriendsDao friendsDao, @Qualifier("dbUserStorage") UserStorage userStorage) {
        this.friendsDao = friendsDao;
        this.userStorage = userStorage;
    }

    @Override
    public void addFriend(long userId, long friendUserId) {
        friendsDao.addFriend(userId, friendUserId);
    }

    @Override
    public List<User> getFriendList(long userId) {
        List<User> friendList = new ArrayList<>();
        for (Long friendId : friendsDao.getUserAllFriendsId(userId)) {
            friendList.add(userStorage.findUserById(friendId));
        }
        return friendList;
    }

    @Override
    public void deleteFriend(long userId, long friendUserId) {
        friendsDao.deleteFriend(userId, friendUserId);
    }

    @Override
    public List<User> getListMutualFriends(long userId, long friendUserId) {
        checkUserAndFriendId(userId, friendUserId);
        List<Long> mutualFriendsId = userStorage.findUserById(userId).getFriendsId().stream()
                .filter(userStorage.findUserById(friendUserId).getFriendsId()::contains)
                .collect(toList());
        List<User> mutualFriends = new ArrayList<>();
        for (long mutualId : mutualFriendsId) {
            mutualFriends.add(userStorage.findUserById(mutualId));
        }
        log.debug("User {} friend {} mutual friends list {}", userId, friendUserId, mutualFriends);
        return mutualFriends;
    }

    @Override
    public UserStorage getUserStorage() {
        return userStorage;
    }


    private void checkUserAndFriendId(Long userId, Long friendId) {
        if (userId == null || friendId == null || userId <= 0 || friendId <= 0) {
            log.debug("Check user {} check friend {}", userId, friendId);
            throw new IdNotFoundException(String.format("User with id:%s or user friend with id:%s not found",
                    userId, friendId));
        }
    }
}
