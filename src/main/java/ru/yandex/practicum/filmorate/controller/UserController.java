package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.serviseinterface.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
   private final UserService userService;

    public UserController(@Qualifier("UserServiceDb") UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.getUserStorage().addUser(user);
    }


    @GetMapping
    public Collection<User> getAll() {
        return userService.getUserStorage().getAllUsers();
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        return userService.getUserStorage().updateUser(user);
    }

    @GetMapping("/{userId}")
    public User findUser(@PathVariable("userId") long userId) {
        return userService.getUserStorage().findUserById(userId);
    }

    @DeleteMapping
    public void deleteAllUsers() {
        userService.getUserStorage().deleteAllUsers();
    }

    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable("userId") long userId) {
        userService.getUserStorage().deleteUser(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable("userId") long userId,
                          @PathVariable("friendId") long friendId) {
        userService.addFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable("userId") long userId) {
        return userService.getFriendList(userId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable("userId") long userId,
                             @PathVariable("friendId") long friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends/common/{friendsId}")
    public List<User> getMutualFriends(@PathVariable long userId,
                                       @PathVariable long friendsId) {
        return userService.getListMutualFriends(userId, friendsId);
    }
}