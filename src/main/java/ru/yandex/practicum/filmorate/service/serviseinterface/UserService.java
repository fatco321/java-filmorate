package ru.yandex.practicum.filmorate.service.serviseinterface;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userstorage.storageinterface.UserStorage;

import java.util.List;

public interface UserService {
    void addFriend(long userId, long friendUserId);
    
    List<User> getFriendList(long userId);
    
    void deleteFriend(long userId, long friendUserId);
    
    List<User> getListMutualFriends(long userId, long friendUserId);
    
    UserStorage getUserStorage();
}
