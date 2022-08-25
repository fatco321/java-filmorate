package ru.yandex.practicum.filmorate.storage.filmstorage.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.MpaDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@Slf4j
public class MpaDaoImp implements MpaDao {
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public MpaDaoImp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public Collection<Mpa> getAllMpa() {
        String sql = "select * from mpa_ratings";
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }
    
    @Override
    public Mpa getMpaFromDb(int mpaId) {
        if (noExists(mpaId)) {
            log.debug("getting mpa rating with incorrect id {}", mpaId);
            throw new IdNotFoundException(String.format("MPA rating with id:%s not found", mpaId));
        }
        String sql = "select * from mpa_ratings where rating_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToMpa, mpaId);
    }
    
    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder().id(resultSet.getInt("rating_id")).name(resultSet.getString("rating"))
            .build();
    }
    
    private boolean noExists(int mpaId) {
        String sql = "select count (*) from mpa_ratings where rating_id = ?";
        int result = jdbcTemplate.queryForObject(sql, Integer.class, mpaId);
        return result == 0;
    }
}
