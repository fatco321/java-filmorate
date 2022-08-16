package ru.yandex.practicum.filmorate.storage.filmstorage.database;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmLikeDao;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.GenreDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component("dbFilmStorage")
@Getter
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDaoImp mpaDao;
    private final GenreDao genreDao;
    private final FilmLikeDao filmLikeDao;
    private static final LocalDate FIRST_FILM_RELEASE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDaoImp mpaDao,
                         GenreDaoImp genreDaoImp, FilmLikeDaoImp filmLikeDaoImp) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDao = mpaDao;
        this.genreDao = genreDaoImp;
        this.filmLikeDao = filmLikeDaoImp;
    }

    @Override
    public Film addFilm(Film film) {
        validate(film);
        if (film.getMpa() != null) {
            film.setMpa(mpaDao.getMpaFromDb(film.getMpa().getId()));
        }
        film.setId((saveFilmAndReturnId(film)));
        if (film.getGenres() != null) {
            genreDao.addFilmsGenres(film);
        }
        log.debug("add film {}", film.getId());
        return film;
    }

    @Override
    public void deleteFilm(long filmId) {
        if (noExists(filmId)) {
            log.debug("try delete film {} with incorrect id", filmId);
            throw new IdNotFoundException(String.format("film with id:%s not found", filmId));
        }
        String sql = "DELETE FROM FILMS WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        if (noExists(film.getId())) {
            log.debug("try update film {} with incorrect id", film.getId());
            throw new IdNotFoundException(String.format("film with id:%s not found", film.getId()));
        }
        if (film.getGenres() != null) {
            genreDao.updateFilmsGenres(film);
        }
        String sql = "update FILMS set " +
                "FILM_NAME = ?, FILM_DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, FILM_RATE = ?, MPA_ID = ? " +
                "where FILM_ID = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getRate(), film.getMpa().getId(), film.getId());
       /* пришлось так сделать чтоб пройти тест (Friend film genres update with duplicate) postman
        т.к. он чувствителен к порядку выдачи жанров.*/
        return findFilmById(film.getId());
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "select * from films;";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public void deleteAllFilms() {
        String sql = "delete from films";
        jdbcTemplate.update(sql);
    }

    @Override
    public Film findFilmById(long filmId) {
        if (noExists(filmId)) {
            log.debug("getting film {} with incorrect id", filmId);
            throw new IdNotFoundException(String.format("film with id:%s not found", filmId));
        }
        String sql = "select * from films where film_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToFilm, filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count, int genreId, int year) {
        if (genreId > genreDao.getAllGenres().size()) {
            throw new IdNotFoundException(String.format("Genre with id %s not found", genreId));
        }
        if (year > LocalDate.now().getYear()) {
            throw new IdNotFoundException(String.format("Incorrect film release date %s", year));
        }
        String sql = "select f.* from  FILMS f left join FILMS_LIKES FL on f.FILM_ID = FL.FILM_ID " +
                "group by  f.FILM_ID order by count(USER_ID) desc  limit  ?";
        if (genreId != 0 && year == 0) {
            sql = "select f.* from  FILMS f left join FILMS_LIKES FL on f.FILM_ID = FL.FILM_ID " +
                    "left join FILMS_GENRES FG on f.FILM_ID = FG.FILM_ID " +
                    "where GENRE_ID = ? group by f.FILM_ID order by count(USER_ID) limit ?";
            return jdbcTemplate.query(sql, this::mapRowToFilm, genreId, count);
        }
        if (genreId == 0 && year != 0) {
            sql = "select f.* from  FILMS f left join FILMS_LIKES FL on f.FILM_ID = FL.FILM_ID " +
                    "where extract(year from f.RELEASE_DATE) = ? group by  f.FILM_ID " +
                    "order by count(USER_ID) desc  limit  ?";
            return jdbcTemplate.query(sql, this::mapRowToFilm, year, count);
        }
        if (genreId != 0 && year != 0) {
            sql = "select f.* from  FILMS f left join FILMS_LIKES FL on f.FILM_ID = FL.FILM_ID " +
                    "left join FILMS_GENRES FG on f.FILM_ID = FG.FILM_ID " +
                    "where GENRE_ID = ? and extract(year from f.RELEASE_DATE) = ? " +
                    "group by f.FILM_ID order by count(USER_ID) limit ?";
            return jdbcTemplate.query(sql, this::mapRowToFilm, genreId, year, count);
        }
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    private long saveFilmAndReturnId(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films").usingGeneratedKeyColumns("film_id");
        return simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
    }


    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE)) {
            log.debug("film not valid release date:{}", film.getReleaseDate());
            throw new ValidationException("Release date not valid");
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("film_name"))
                .description(resultSet.getString("film_description"))
                .releaseDate(LocalDate.parse(resultSet.getString("release_date")))
                .duration(resultSet.getInt("duration"))
                .mpa(mpaDao.getMpaFromDb(resultSet.getInt("mpa_id")))
                .rate(resultSet.getInt("film_rate"))
                .genres(getFilmGenres((int) resultSet.getLong("film_id")))
                .usersLike(filmLikeDao.getUserLikes((int) resultSet.getLong("film_id"))).build();
    }

    private Set<Genre> getFilmGenres(int rowNum) {
        Set<Genre> genres = new HashSet<>();
        String sql = "select GENRE_ID from FILMS_GENRES where FILM_ID = ?";
        List<Integer> listGenreId = jdbcTemplate.queryForList(sql, Integer.class, rowNum);
        for (int genreId : listGenreId) {
            genres.add(genreDao.getGenreFromDb(genreId));
        }
        return genres;
    }

    private boolean noExists(long id) {
        String sql = "select count(*) from FILMS where FILM_ID = ?";
        int result = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return result == 0;
    }
}
