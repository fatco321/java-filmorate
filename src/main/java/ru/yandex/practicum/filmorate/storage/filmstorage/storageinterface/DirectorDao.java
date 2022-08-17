package ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Set;

public interface DirectorDao {
    
    List<Director> getAllDirectors();
    
    Director getDirectorById(long id);
    
    void saveDirector(Director director);
    
    void updateDirector(Director director);
    
    void deleteDirector(long id);
    
    Set<Director> getDirectorsForFilm(long filmId);
    
    void addDirectorsToFilm(long filmId, Set<Director> directors);
    
    boolean isDirectorExist(long id);
}
