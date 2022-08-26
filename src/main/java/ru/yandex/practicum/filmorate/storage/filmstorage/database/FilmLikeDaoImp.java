package ru.yandex.practicum.filmorate.storage.filmstorage.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmLikeNotFoundException;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmLikeDao;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class FilmLikeDaoImp implements FilmLikeDao {
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public FilmLikeDaoImp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public Set<Long> getUserLikes(long filmId) {
        String sql = "select user_id from films_likes where film_id = ?";
        List<Long> usersLike = jdbcTemplate.queryForList(sql, Long.class, filmId);
        return new LinkedHashSet<>(usersLike);
    }
    
    @Override
    public void addLike(long filmId, long userId) {
        String sql = "merge into films_likes key (film_id, user_id) " +
            "values (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
        log.debug("add like to film {} from user {}", filmId, userId);
    }
    
    @Override
    public void deleteLike(long filmId, long userId) {
        checkFilmId(filmId);
        String sql = "delete from films_likes where user_id = ? and film_id = ?";
        jdbcTemplate.update(sql, userId, filmId);
        log.debug("delete film {} like from user{}", filmId, userId);
    }
    
    private void checkFilmId(long id) {
        String sql = "select count(*) from films_likes where film_id = ?";
        int result = jdbcTemplate.queryForObject(sql, Integer.class, id);
        if (result == 0) {
            throw new FilmLikeNotFoundException(String.format("film with id:%s not found", id));
        }
    }

    @Override
    public Set<Long> getUserFilmLikes(long userId) {
        String sql = "select film_id from films_likes where user_id = ?";
        List<Long> usersLike = jdbcTemplate.queryForList(sql, Long.class, userId);
        return new LinkedHashSet<>(usersLike);
    }

}
