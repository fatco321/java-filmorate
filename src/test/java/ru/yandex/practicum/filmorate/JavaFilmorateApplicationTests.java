package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.filmstorage.database.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.GenreDao;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.MpaDao;
import ru.yandex.practicum.filmorate.storage.userstorage.storageinterface.FriendsDao;
import ru.yandex.practicum.filmorate.storage.userstorage.database.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JavaFilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FriendsDao friendsDao;
    private final MpaDao mpaDao;
    private final GenreDao genreDao;
    private final FilmDbStorage filmDbStorage;
    private final User user = new User(1, "yandex@ya.ru", "yandex", "Test",
            LocalDate.of(2000, 1, 1), new HashSet<>());
    private final Film film = new Film(1, "Test flim", "Test", LocalDate.of(2000, 1, 1),
            100, new HashSet<>(), new HashSet<>(), Mpa.builder().id(1).build(), 3);

    @Test
    public void testFindUserById() {
        userStorage.addUser(user);
        Optional<User> userOptional = Optional.ofNullable(userStorage.findUserById(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testUpdateUser() {
        userStorage.addUser(user);
        User userUp = new User(1, "1@2.ru", "yandex1", "test", LocalDate.of(2000, 1, 1), new HashSet<>());
        userStorage.updateUser(userUp);
        assertEquals(userUp, userStorage.findUserById(1L));
    }

    @Test
    public void testDeleteUserIncorrectId() {
        User userUp = new User(132, "1@2.ru", "yandex1", "test", LocalDate.of(2000, 1, 1), new HashSet<>());
        assertThrows(IdNotFoundException.class, () -> userStorage.updateUser(userUp));
    }

    @Test
    public void testDeleteUser() {
        userStorage.deleteUser(1);
        assertThrows(IdNotFoundException.class, () -> userStorage.findUserById(1));
    }

    @Test
    public void testDddFriend() {
        User friend = new User(1, "f@a.ru,", "f", "fa", LocalDate.of(1999, 2, 1), new HashSet<>());
        userStorage.addUser(friend);
        friendsDao.addFriend(1, 2);
        assertTrue(userStorage.findUserById(1).getFriendsId().contains(friendsDao.getFriends(1).get(0)));
    }

    @Test
    public void testAddFriendsIncorrectId() {
        assertThrows(IdNotFoundException.class, () -> friendsDao.deleteFriend(1, 23));
    }

    @Test
    public void testGetAllFriendsIdWithIncorrectId() {
        assertThrows(IdNotFoundException.class, () -> friendsDao.getUserAllFriendsId(123));
    }

    @Test
    public void testGetAllMpaRatings() {
        assertEquals(5, mpaDao.getAllMpa().size());
    }

    @Test
    public void testGetMpaRatingsById() {
        assertEquals("G", mpaDao.getMpaFromDb(1).getName());
    }

    @Test
    public void testGetMpaRatingsWithIncorrectId() {
        assertThrows(IdNotFoundException.class, () -> mpaDao.getMpaFromDb(123));
    }

    @Test
    public void testGetAllGenres() {
        assertEquals(6, genreDao.getAllGenres().size());
    }

    @Test
    public void testGetGenreById() {
        assertEquals("Комедия", genreDao.getGenreFromDb(1).getName());
    }

    @Test
    public void testAddFilmAndReturn() {
        filmDbStorage.addFilm(film);
        assertEquals(film, filmDbStorage.findFilmById(3));
    }

    @Test
    public void testFindFilmIncorrectID() {
        assertThrows(IdNotFoundException.class, () -> filmDbStorage.findFilmById(123));
    }

    @Test
    public void testUpdateFilm() {
        filmDbStorage.addFilm(film);
        film.setName("Movie");
        filmDbStorage.updateFilm(film);
        assertEquals(film.getName(), filmDbStorage.findFilmById(1).getName());
    }

    @Test
    public void testUpdateFilmWithIncorrectId() {
        film.setId(32);
        assertThrows(IdNotFoundException.class, () -> filmDbStorage.updateFilm(film));
    }

    @Test
    public void testDeleteAllFilm() {
        filmDbStorage.deleteAllFilms();
        assertTrue(filmDbStorage.getAllFilms().isEmpty());
    }

    @Test
    public void testDeleteFilm() {
        filmDbStorage.addFilm(film);
        filmDbStorage.deleteFilm(2);
        assertTrue(filmDbStorage.getAllFilms().isEmpty());
    }
}