package ru.yandex.practicum.filmorate.exception_handler.exceptions;

public class DirectorNotFound extends RuntimeException{
    public DirectorNotFound (String message) {
        super(message);
    }
}
