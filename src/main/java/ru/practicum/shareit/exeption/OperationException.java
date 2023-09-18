package ru.practicum.shareit.exeption;

public class OperationException extends RuntimeException {
    public OperationException(String message) {
        super(message);
    }
}