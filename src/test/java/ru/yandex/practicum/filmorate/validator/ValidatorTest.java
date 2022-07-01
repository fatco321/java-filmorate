package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ValidatorTest {
    private final FilmController filmController = new FilmController();
    private final UserController userController = new UserController();

    @Test
    public void test01_FilmValidateWithCorrectModel() {
        Film film = new Film(1, "Test Name", "Test",
                LocalDate.of(2000, 12, 1), 20);
        filmController.validate(film);
    }

    @Test
    public void test02_FilmValidateWithInCorrectReleaseDate() {
        Film film = new Film(1, "Test Name", "Test",
                LocalDate.of(1000, 12, 1), 20);
        assertThrows(ValidationException.class, () -> filmController.validate(film));
    }

    @Test
    public void test04_UserValidateWithCorrectModel() {
        User user = new User(1, "yandex@ya.ru", "yandex", "Test",
                LocalDate.of(2000, 1, 1));
        userController.validate(user);
    }

    @Test
    public void test06_UserValidateWithEmptyName() {
        User user = new User(1, "yandex@ya.ru", "yandex", "",
                LocalDate.of(2000, 1, 1));
        userController.validate(user);
        assertEquals(user.getName(), user.getLogin());
    }
}
