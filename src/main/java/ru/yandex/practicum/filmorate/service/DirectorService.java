package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.filmstorage.storageinterface.DirectorDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    
    private final DirectorDao directorDao;
    
    public List<Director> getAllDirectors() {
        return directorDao.getAllDirectors();
    }
    
    public Director getDirectorById(long id) {
        return directorDao.getDirectorById(id);
    }
    
    public void saveDirector(Director director) {
        directorDao.saveDirector(director);
    }
    
    public void updateDirector(Director director) {
        directorDao.updateDirector(director);
    }
    
    public void deleteDirector(long id) {
        directorDao.deleteDirector(id);
    }
}
