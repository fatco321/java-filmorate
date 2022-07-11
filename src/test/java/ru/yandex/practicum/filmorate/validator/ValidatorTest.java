package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.filmstorage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.userstorage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class ValidatorTest {
    private final InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
    private final InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();

    @Test
    public void test01_FilmValidateWithCorrectModel() {
        Film film = new Film(1, "Test Name", "Test",
                LocalDate.of(2000, 12, 1), 20, new HashSet<>());
        assertEquals(film, inMemoryFilmStorage.addFilm(film));
    }

    @Test
    public void test02_FilmValidateWithInCorrectReleaseDate() {
        Film film = new Film(1, "Test Name", "Test",
                LocalDate.of(1000, 12, 1), 20, new HashSet<>());
        assertThrows(ValidationException.class, () -> inMemoryFilmStorage.addFilm(film));
    }

    @Test
    public void test04_UserValidateWithCorrectModel() {
        User user = new User(1, "yandex@ya.ru", "yandex", "Test",
                LocalDate.of(2000, 1, 1), new HashSet<>());
        assertEquals(user, inMemoryUserStorage.addUser(user));
    }

    @Test
    public void test06_UserValidateWithEmptyName() {
        User user = new User(1, "yandex@ya.ru", "yandex", "",
                LocalDate.of(2000, 1, 1), new HashSet<>());
        inMemoryUserStorage.addUser(user);
        assertEquals(user.getName(), user.getLogin());
    }
}
