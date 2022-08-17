package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;
    
    @GetMapping
    public List<Director> getAllDirectors() {
        return directorService.getAllDirectors();
    }
    
    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable long id) {
        return directorService.getDirectorById(id);
    }
    
    @PostMapping
    public Director postDirector(@Valid @RequestBody Director director) {
        directorService.saveDirector(director);
        return director;
    }
    
    @PutMapping
    public Director putDirector(@Valid @RequestBody Director director) {
        directorService.updateDirector(director);
        return director;
    }
    
    @DeleteMapping("/{id}")
    public void deleteDirectorById(@PathVariable long id) {
        directorService.deleteDirector(id);
    }
}
