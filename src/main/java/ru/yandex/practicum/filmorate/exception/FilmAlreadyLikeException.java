package ru.yandex.practicum.filmorate.exception;

public class FilmAlreadyLikeException extends RuntimeException {

    public FilmAlreadyLikeException(String message) {
        super(message);
    }
}
