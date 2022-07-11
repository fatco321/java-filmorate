package ru.yandex.practicum.filmorate.exception;

public class AlreadyUseException extends RuntimeException{

    public AlreadyUseException(String message) {
        super(message);
    }
}
