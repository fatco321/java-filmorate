package ru.yandex.practicum.filmorate.storage.filmstorage.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.FilmMarkDao;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class FilmMarkDaoImp implements FilmMarkDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmMarkDaoImp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isExistFilmMarkById(long filmId, long userId) {
        String sql = "select count(*) from films_marks where film_id = ? and user_id = ?";
        int result = jdbcTemplate.queryForObject(sql, Integer.class, filmId, userId);
        return result > 0;
    }

    @Override
    public void addMark(long filmId, long userId, int mark) {
        String sql = "insert into films_marks (film_id, user_id, mark) " +
                "values (?, ?, ?)";
        jdbcTemplate.update(sql, filmId, userId, mark);
        log.debug("add mark {} to film {} from user {}", mark, filmId, userId);
    }

    @Override
    public void deleteMark(long filmId, long userId) {
        String sql = "delete from films_marks where user_id = ? and film_id = ?";
        jdbcTemplate.update(sql, userId, filmId);
        log.debug("delete film {} like from user{}", filmId, userId);
    }

    @Override
    public double getFilmMarkAvgById(long filmId) {
        try {
            String sql = "select avg(mark) from films_marks where film_id = ?";
            return jdbcTemplate.queryForObject(sql, Double.class, filmId);
        } catch (Throwable ignored) {}
        return 0;
    }

    @Override
    public Set<Long> getUserFilmsWithPositiveMark(long userId) {
        String sql = "select film_id from films_marks where user_id = ? and mark > 5";
        List<Long> usersLike = jdbcTemplate.queryForList(sql, Long.class, userId);
        return new LinkedHashSet<>(usersLike);
    }

}
