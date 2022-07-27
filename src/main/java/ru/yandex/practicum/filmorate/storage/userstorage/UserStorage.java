package ru.yandex.practicum.filmorate.storage.userstorage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User addUser(User user);

    void deleteUser(long userId);

    User updateUser(User user);

    Collection<User> getAllUsers();

    void deleteAllUsers();

    User findUserById(Long id);
}
