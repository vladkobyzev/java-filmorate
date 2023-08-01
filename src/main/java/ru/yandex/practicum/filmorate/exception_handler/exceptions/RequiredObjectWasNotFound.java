package ru.yandex.practicum.filmorate.exception_handler.exceptions;

import java.util.function.Supplier;

public class RequiredObjectWasNotFound extends RuntimeException {
    public RequiredObjectWasNotFound (String message) {
        super(message);
    }
}
