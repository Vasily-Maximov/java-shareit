package ru.practicum.shareit.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFoundException(final ObjectNotFoundException e) {
        log.info(String.format("error: %s", e.getMessage()));
        return new ErrorResponse(String.format("error: %s", e.getMessage()));
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleObjectValidationException(final ObjectValidationException e) {
        log.info(String.format("error: %s", e.getMessage()));
        return new ErrorResponse(String.format("error: %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.info(String.format("Поле:= %s - %s", Objects.requireNonNull(e.getFieldError()).getField(),
                e.getFieldError().getDefaultMessage()));
        return new ErrorResponse(String.format("Поле:= %s - %s", Objects.requireNonNull(e.getFieldError()).getField(),
                e.getFieldError().getDefaultMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameter(final NumberFormatException e) {
        String message = String.format("Некорректно передан параметр: %s", e.getMessage());
        log.info(message);
        return new ErrorResponse(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleOperationException(final OperationException e) {
        String message = String.format("Ошибка операции: %s", e.getMessage());
        log.info(message);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.info("Произошла непредвиденная ошибка.");
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}