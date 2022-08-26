package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.service.serviseinterface.UserService;
import ru.yandex.practicum.filmorate.storage.feedstorage.storageinterface.FeedDao;
import ru.yandex.practicum.filmorate.storage.userstorage.storageinterface.FriendsDao;
import ru.yandex.practicum.filmorate.storage.userstorage.storageinterface.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service("UserServiceDb")
@Slf4j
@Getter
@RequiredArgsConstructor
public class UserServiceDb implements UserService {
    private final FriendsDao friendsDao;
    private final UserStorage userStorage;
    private final FeedDao feedDao;
    
    @Override
    public void addFriend(long userId, long friendUserId) {
        friendsDao.addFriend(userId, friendUserId);
        feedDao.createFeed(userId,friendUserId,EventType.FRIEND,Operation.ADD);
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
        feedDao.createFeed(userId,friendUserId,EventType.FRIEND,Operation.REMOVE);
    }
    
    @Override
    public List<User> getListMutualFriends(long userId, long friendUserId) {
        checkUserAndFriendId(userId, friendUserId);
        return userStorage.getListMutualFriends(userId, friendUserId);
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
