package ru.yandex.practicum.filmorate.storage.filmstorage.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.DirectorDao;
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
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDaoImp mpaDao;
    private final GenreDao genreDao;
    private final FilmLikeDao filmLikeDao;
    private final DirectorDao directorDao;

    @Override
    public Film addFilm(Film film) {
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
        return findFilmById(film.getId());
    }

    @Override
    public void deleteFilm(long filmId) {
        if (noExists(filmId)) {
            log.debug("try delete film {} with incorrect id", filmId);
            throw new IdNotFoundException(String.format("film with id:%s not found", filmId));
        }
        String sql = "delete from films where film_id = ?";
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
        String sql = "update films set " +
                "film_name = ?, " +
                "film_description = ?, " +
                "release_date = ?, " +
                "duration = ?, " +
                "film_rate = ?, " +
                "mpa_id = ? " +
                "where film_id = ?";
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
    public List<Film> getPopularFilms(int count, int genreId, int year) {
        if (genreId > genreDao.getAllGenres().size()) {
            throw new IdNotFoundException(String.format("Genre with id %s not found", genreId));
        }
        if (year > LocalDate.now().getYear()) {
            throw new IdNotFoundException(String.format("Incorrect film release date %s", year));
        }
        String sql = "select f.* from  films f left join films_likes fl on f.film_id = fl.film_id " +
                "group by  f.film_id order by count(user_id) desc  limit  ?";
        if (genreId != 0 && year == 0) {
            sql = "select f.* from  films f left join films_likes fl on f.film_id = fl.film_id " +
                    "left join films_genres fg on f.film_id = fg.film_id " +
                    "where genre_id = ? group by f.film_id order by count(user_id) limit ?";
            return jdbcTemplate.query(sql, this::mapRowToFilm, genreId, count);
        }
        if (genreId == 0 && year != 0) {
            sql = "select f.* from  films f left join films_likes fl on f.film_id = fl.film_id " +
                    "where extract(year from f.release_date) = ? group by  f.film_id " +
                    "order by count(user_id) desc  limit  ?";
            return jdbcTemplate.query(sql, this::mapRowToFilm, year, count);
        }
        if (genreId != 0 && year != 0) {
            sql = "select f.* from  films f left join films_likes fl on f.film_id = fl.film_id " +
                    "left join films_genres fg on f.film_id = fg.film_id " +
                    "where genre_id = ? and extract(year from f.release_date) = ? " +
                    "group by f.film_id order by count(user_id) limit ?";
            return jdbcTemplate.query(sql, this::mapRowToFilm, genreId, year, count);
        }
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    @Override
    public List<Film> getDirectorFilms(long directorId, String sortBy) {
        if (directorDao.isDirectorExist(directorId)) {
            String sqlQuery;
            switch (sortBy) {
                case "likes": {
                    sqlQuery = "with a as (select director_id, " +
                            "d.film_id, " +
                            "count(distinct user_id) as cnt_users_liked " +
                            "from film_directors as d " +
                            "left join films_likes as l on d.film_id = l.film_id " +
                            "group by director_id, " +
                            "d.film_id " +
                            "order by cnt_users_liked desc ) " +
                            "select director_id, " +
                            "a.cnt_users_liked, " +
                            "f.film_id, " +
                            "film_name, " +
                            "film_description, " +
                            "release_date, duration, " +
                            "film_rate, " +
                            "mpa_id, " +
                            "director_id " +
                            "from a " +
                            "left join films as f on a.film_id = f.film_id " +
                            "where director_id = ?";
                    break;
                }
                case "year": {
                    sqlQuery = "select f.film_id, " +
                            "film_name, " +
                            "film_description, " +
                            "release_date, duration, " +
                            "film_rate, " +
                            "mpa_id, " +
                            "director_id " +
                            "from films f " +
                            "join film_directors fd on f.film_id = fd.film_id " +
                            "where fd.director_id = ? " +
                            "order by f.release_date";
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

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        String sql = "select f.* from films f, films_likes fl1, films_likes fl2 " +
                "where fl1.user_id = ? and fl2.user_id = ? " +
                "and f.film_id = fl1.film_id and f.film_id = fl2.film_id";
        return jdbcTemplate.query(sql, this::mapRowToFilm, userId, friendId);
    }

    @Override
    public List<Film> searchFilms(String query, boolean title, boolean director) {
        if (title && !director) {
            return searchByTitle(query);
        }
        if (!title && director) {
            return searchByDirector(query);
        }
        List<Film> searchAnyway = new ArrayList<>();
        searchAnyway.addAll(searchByDirector(query));
        searchAnyway.addAll(searchByTitle(query));
        if (searchByDirector(query).isEmpty() || searchByTitle(query).isEmpty()) {
            return new ArrayList<>();
        }
        return searchAnyway;
    }

    private List<Film> searchByTitle(String query) {
        String sql = "select * from  films where lower(film_name) like ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, "%" + query.toLowerCase() + "%");
    }

    private List<Film> searchByDirector(String query) {
        String sql = "select f.* from films f, directors d, film_directors fd " +
                "where f.film_id = fd.film_id and fd.director_id = d.director_id and lower(director_name) like ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, "%" + query.toLowerCase() + "%");
    }

    private long saveFilmAndReturnId(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films").usingGeneratedKeyColumns("film_id");
        return simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
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
        String sql = "select genre_id from films_genres where film_id = ?";
        List<Integer> listGenreId = jdbcTemplate.queryForList(sql, Integer.class, rowNum);
        for (int genreId : listGenreId) {
            genres.add(genreDao.getGenreFromDb(genreId));
        }
        return genres;
    }

    private boolean noExists(long id) {
        String sql = "select count(*) from films where film_id = ?";
        int result = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return result == 0;
    }
}
