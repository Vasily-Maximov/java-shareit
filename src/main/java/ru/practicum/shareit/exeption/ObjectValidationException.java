package ru.practicum.shareit.exeption;

public class ObjectValidationException extends RuntimeException {

    public ObjectValidationException(String message) {
        super(message);
    }
}