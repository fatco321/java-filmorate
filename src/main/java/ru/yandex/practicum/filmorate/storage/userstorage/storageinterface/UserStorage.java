package ru.yandex.practicum.filmorate.storage.userstorage.storageinterface;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    
    User addUser(User user);
    
    void deleteUser(long userId);
    
    User updateUser(User user);
    
    Collection<User> getAllUsers();
    
    void deleteAllUsers();
    
    User findUserById(long id);
    
    List<User> getListMutualFriends(long userId, long friendUserId);
}
