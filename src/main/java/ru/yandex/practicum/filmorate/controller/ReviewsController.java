package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.serviseinterface.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/reviews")
public class ReviewsController {
    private final ReviewService reviewService;
    
    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        log.info("POST review {}", review);
        return reviewService.addReview(review);
    }
    
    @PutMapping
    public Review put(@Valid @RequestBody Review review) {
        log.info("PUT review {}", review);
        return reviewService.updateReview(review);
    }
    
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable long id) {
        log.info("DELETE review by id {}", id);
        reviewService.deleteReview(id);
    }
    
    @GetMapping("/{id}")
    public Review findUser(@PathVariable long id) {
        log.info("GET review by id {}", id);
        return reviewService.getReviewById(id);
    }
    
    @GetMapping
    public List<Review> getListReviews(@RequestParam(defaultValue = "0", required = false) long filmId,
                                       @RequestParam(defaultValue = "10", required = false) Integer count) {
        log.info("GET {} reviews", count);
        return reviewService.getListReview(filmId, count);
    }
    
    @PutMapping("/{id}/like/{userId}")
    public void addLikeToReview(@PathVariable("id") long reviewId,
                                @PathVariable("userId") long userId) {
        log.info("user id {} like review id {}", userId, reviewId);
        reviewService.addLikeToReview(reviewId, userId);
    }
    
    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable("id") long reviewId,
                                   @PathVariable("userId") long userId) {
        log.info("user id {} dislike review id {}", userId, reviewId);
        reviewService.addDislikeToReview(reviewId, userId);
    }
    
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeToReview(@PathVariable("id") long reviewId,
                                   @PathVariable("userId") long userId) {
        log.info("DELETE like user id {} from review id {}", userId, reviewId);
        reviewService.deleteLikeToReview(reviewId, userId);
    }
    
    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeToReview(@PathVariable("id") long reviewId,
                                      @PathVariable("userId") long userId) {
        log.info("DELETE dislike user id {} from review id {}", userId, reviewId);
        reviewService.deleteDislikeToReview(reviewId, userId);
    }
    
}