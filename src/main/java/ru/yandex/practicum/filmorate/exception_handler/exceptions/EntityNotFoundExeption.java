package ru.yandex.practicum.filmorate.exception_handler.exceptions;

public class EntityNotFoundExeption extends Exception{
    public EntityNotFoundExeption (String message) {
        super(message);
    }
}