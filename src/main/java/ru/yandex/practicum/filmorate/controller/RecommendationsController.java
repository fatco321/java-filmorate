package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.RecommendationsService;

import java.util.List;
/**
 * Контроллер рекомендаций
 */
@RestController
@RequestMapping("/users")
public class RecommendationsController {
    private final RecommendationsService recommendationsService;

    public RecommendationsController(RecommendationsService recommendationsService) {
        this.recommendationsService = recommendationsService;
    }

    @GetMapping("/{userId}/recommendations")
    public List<Film> getFriends(@PathVariable("userId") long userId) {
        return recommendationsService.getRecommendations(userId);
    }
}
