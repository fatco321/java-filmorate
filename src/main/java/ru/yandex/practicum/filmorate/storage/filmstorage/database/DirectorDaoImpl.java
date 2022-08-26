package ru.yandex.practicum.filmorate.storage.filmstorage.database;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.DirectorDao;

import java.sql.ResultSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class DirectorDaoImpl implements DirectorDao {
    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public List<Director> getAllDirectors() {
        String sqlQuery = "select director_id, " +
            "director_name " +
            "from directors";
        
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeDirector(rs));
    }
    
    @Override
    public Director getDirectorById(long id) {
        String sqlQuery = "select director_id, " +
            "director_name " +
            "from directors " +
            "where director_id = ?";
        
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeDirector(rs), id)
            .stream()
            .findAny()
            .orElseThrow(() -> {
                throw new IdNotFoundException(String
                    .format("Director with id: %s not found.", id));
            });
    }
    
    @Override
    public void saveDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("DIRECTORS")
            .usingGeneratedKeyColumns("DIRECTOR_ID");
        
        director
            .setId(simpleJdbcInsert
                .executeAndReturnKey(director
                    .toMap())
                .longValue());
    }
    
    @Override
    public void updateDirector(Director director) {
        String sqlQuery = "update directors set " +
            "director_name = ? " +
            "where director_id = ?";
        
        if (isDirectorExist(director.getId())) {
            jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        } else {
            throw new IdNotFoundException(String
                .format("Director with id: %s not found.", director.getId()));
        }
    }
    
    @Override
    public void deleteDirector(long id) {
        if (isDirectorExist(id)) {
            String sqlQuery = "delete from directors where director_id = ?";
            jdbcTemplate.update(sqlQuery, id);
        } else {
            throw new IdNotFoundException(String
                .format("Director with id: %s not found.", id));
        }
    }
    
    @Override
    public boolean isDirectorExist(long id) {
        String sqlQuery = "select * from directors where director_id = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeDirector(rs), id).stream().findAny().isPresent();
    }
    
    @Override
    public Set<Director> getDirectorsForFilm(long filmId) {
        String sqlQuery = "select d.director_id, " +
            "director_name " +
            "from film_directors fd " +
            "join directors d on fd.director_id = d.director_id " +
            "where film_id = ?";
        return new LinkedHashSet<>(jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeDirector(rs), filmId));
    }
    
    @Override
    public void addDirectorsToFilm(long filmId, Set<Director> directors) {
        jdbcTemplate.update("delete from film_directors where film_id = ?", filmId);
        
        if (directors != null) {
            StringBuilder sqlQuery = new StringBuilder("insert into FILM_DIRECTORS values");
            for (Director item : directors) {
                sqlQuery.append(String.format(" ('%s', '%s'),", filmId, item.getId()));
            }
            sqlQuery.deleteCharAt(sqlQuery.length() - 1);
            jdbcTemplate.update(sqlQuery.toString());
        }
    }
    
    @SneakyThrows
    private Director makeDirector(ResultSet rs) {
        return Director.builder()
            .id(rs.getLong("DIRECTOR_ID"))
            .name(rs.getString("DIRECTOR_NAME"))
            .build();
    }
}
