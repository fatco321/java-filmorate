package ru.yandex.practicum.filmorate.storage.filmstorage.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.DirectorDao;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmLikeDao;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.GenreDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("dbFilmStorage")
@Getter
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDaoImp mpaDao;
    private final GenreDao genreDao;
    private final FilmLikeDao filmLikeDao;
    private final DirectorDao directorDao;
    private static final LocalDate FIRST_FILM_RELEASE = LocalDate.of(1895, 12, 28);
    
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
        if (film.getDirectors() != null) {
            directorDao.addDirectorsToFilm(film.getId(), film.getDirectors());
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
        directorDao.addDirectorsToFilm(film.getId(), film.getDirectors());
        String sql = "update FILMS set " +
            "FILM_NAME = ?, " +
            "FILM_DESCRIPTION = ?, " +
            "RELEASE_DATE = ?, " +
            "DURATION = ?, " +
            "FILM_RATE = ?, " +
            "MPA_ID = ? " +
            "where FILM_ID = ?";
        jdbcTemplate.update(sql,
            film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
            film.getRate(), film.getMpa().getId(), film.getId());
       /* пришлось так сделать чтоб пройти тест (Friend film genres update with duplicate) postman
        т.к. он чувствителен к порядку выдачи жанров.*/
        film.setDirectors(directorDao.getDirectorsForFilm(film.getId()));
        film.setGenres(genreDao.getGenresForFilm(film.getId()));
        return film;
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
    public List<Film> getPopularFilms(int count) {
        String sql = "select films.* from  FILMS left join FILMS_LIKES FL on FILMS.FILM_ID = FL.FILM_ID " +
            "group by  films.FILM_ID order by count(USER_ID) desc  limit  ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }
    
    @Override
    public List<Film> getDirectorFilms(long directorId, String sortBy) {
        if (directorDao.isDirectorExist(directorId)) {
            String sqlQuery;
            switch (sortBy) {
                case "likes": {
                    sqlQuery = "with a as (select DIRECTOR_ID, " +
                        "d.FILM_ID, " +
                        "count(distinct USER_ID) as cnt_users_liked " +
                        "from FILM_DIRECTORS as d " +
                        "left join FILMS_LIKES as l on d.FILM_ID = l.FILM_ID " +
                        "group by DIRECTOR_ID, " +
                        "d.FILM_ID " +
                        "order by cnt_users_liked desc ) " +
                        "select DIRECTOR_ID, " +
                        "a.cnt_users_liked, " +
                        "f.FILM_ID, " +
                        "FILM_NAME, " +
                        "FILM_DESCRIPTION, " +
                        "RELEASE_DATE, DURATION, " +
                        "FILM_RATE, " +
                        "MPA_ID, " +
                        "DIRECTOR_ID " +
                        "from a " +
                        "left join FILMS as f on a.FILM_ID = f.FILM_ID " +
                        "where DIRECTOR_ID = ?";
                    break;
                }
                case "year": {
                    sqlQuery = "select f.FILM_ID, " +
                        "FILM_NAME, " +
                        "FILM_DESCRIPTION, " +
                        "RELEASE_DATE, DURATION, " +
                        "FILM_RATE, " +
                        "MPA_ID, " +
                        "DIRECTOR_ID " +
                        "from FILMS f " +
                        "join FILM_DIRECTORS fd on f.FILM_ID = fd.FILM_ID " +
                        "where fd.DIRECTOR_ID = ? " +
                        "order by f.RELEASE_DATE";
                    break;
                }
                default:
                    throw new BadRequestException(String
                        .format("RequestParam sortBy = %s is invalid. Must be \"likes\" or \"year\"", sortBy));
            }
            
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, directorId);
        } else {
            throw new IdNotFoundException(String
                .format("Director with id: %s not found.", directorId));
        }
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
            .usersLike(filmLikeDao.getUserLikes((int) resultSet.getLong("film_id")))
            .directors(directorDao.getDirectorsForFilm((int) resultSet.getLong("film_id")))
            .build();
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
