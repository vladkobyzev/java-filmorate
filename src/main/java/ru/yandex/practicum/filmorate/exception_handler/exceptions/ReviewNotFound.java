package ru.yandex.practicum.filmorate.exception_handler.exceptions;

public class ReviewNotFound extends RuntimeException{
    public ReviewNotFound (String message) {
        super(message);
    }
}
