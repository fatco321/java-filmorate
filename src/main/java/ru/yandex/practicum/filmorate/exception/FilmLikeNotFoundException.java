package ru.yandex.practicum.filmorate.exception;

public class FilmLikeNotFoundException extends RuntimeException{

    public FilmLikeNotFoundException(String message) {
        super(message);
    }
}
