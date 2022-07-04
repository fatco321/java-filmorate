package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IdException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> userMap = new HashMap<>();
    private Integer id = 1;

    private int setId() {
        return id++;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if (userMap.containsKey(user.getId())) {
            log.debug("User id:{}", user.getId());
            throw new IdException("Id already use");
        }
        user.setId(setId());
        validate(user);
        userMap.put(user.getId(), user);
        log.info("User with id:{} create", user.getId());
        return user;
    }

    private void validate(User user) {
        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
            log.debug("User id:{}, with empty name", user.getId());
        }
    }

    @GetMapping
    public Collection<User> getAll() {
        return userMap.values();
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        if (!userMap.containsKey(user.getId())) {
            log.debug("User id:{}", user.getId());
            throw new IdException("Id not found");
        }
        validate(user);
        userMap.put(user.getId(), user);
        log.info("User with id:{} update", user.getId());
        return user;
    }
}