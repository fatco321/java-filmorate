package ru.yandex.practicum.filmorate.storage.filmstorage.database;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.GenreDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
@Slf4j
public class GenreDaoImp implements GenreDao {
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public GenreDaoImp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public Genre getGenreFromDb(int genreId) {
        if (!exist(genreId)) {
            log.debug("getting genre with incorrect id {}", genreId);
            throw new IdNotFoundException(String.format("Genre with id:%s not found", genreId));
        }
        String sql = "select * from genres where GENRE_ID = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToGenre, genreId);
    }
    
    @Override
    public Collection<Genre> getAllGenres() {
        String sql = "select * from GENRES";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }
    
    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder().id(resultSet.getInt("genre_id")).name(resultSet.getString("genre"))
            .build();
    }
    
    @Override
    public void addFilmsGenres(Film film) {
        String sql = "insert into FILMS_GENRES (FILM_ID, GENRE_ID) " +
            "values (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }
    }
    
    @Override
    public void updateFilmsGenres(Film film) {
        deleteFromFilmsGenres(film);
        addFilmsGenres(film);
    }
    
    private boolean exist(int genreId) {
        String sql = "select count(*) from GENRES where GENRE_ID = ?";
        int result = jdbcTemplate.queryForObject(sql, Integer.class, genreId);
        return result == 1;
    }
    
    private void deleteFromFilmsGenres(Film film) {
        String sql = "delete from FILMS_GENRES where FILM_ID = ?";
        jdbcTemplate.update(sql, film.getId());
        
    }
    
    @Override
    public Set<Genre> getGenresForFilm(long filmId) {
        String sqlQuery = "select g.GENRE_ID, " +
            "g.GENRE " +
            "from FILMS_GENRES fg " +
            "join GENRES g on fg.GENRE_ID = g.GENRE_ID " +
            "where FILM_ID = ?";
        return new LinkedHashSet<>(jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeGenre(rs), filmId));
    }
    
    @SneakyThrows
    private Genre makeGenre(ResultSet rs) {
        return Genre.builder()
            .id(rs.getInt("GENRE_ID"))
            .name(rs.getString("GENRE"))
            .build();
    }
}
