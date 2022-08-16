package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIncorrectUserId(final IdNotFoundException e) {
        log.warn(String.format("IdNotFoundException: %s", e.getMessage()));
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotValid(final ValidationException e) {
        log.warn(String.format("ValidationException: %s", e.getMessage()));
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyFriends(final UserFriendException e) {
        log.warn(String.format("UserFriendException: %s", e.getMessage()));
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyUse(final AlreadyUseException e) {
        log.warn(String.format("AlreadyUseException: %s", e.getMessage()));
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleFilmLikesAlready(final FilmAlreadyLikeException e) {
        log.warn(String.format("FilmAlreadyLikeException: %s", e.getMessage()));
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFilmLikeNotFound(final FilmLikeNotFoundException e) {
        log.warn(String.format("FilmLikeNotFoundException: %s", e.getMessage()));
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.warn(String.format("Unexpected error: %s", e.getMessage()));
        return new ErrorResponse("unexpected error");
    }
    
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final BadRequestException e) {
        log.warn(String.format("Bad request: %s", e.getMessage()));
        return new ErrorResponse(String.format("Bad request: %s", e.getMessage()));
    }
}
