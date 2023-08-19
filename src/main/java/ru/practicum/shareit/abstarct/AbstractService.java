package ru.practicum.shareit.abstarct;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exeption.ObjectValidationException;

@Slf4j
public class AbstractService {

    public void checkField(String inputField, String nameField) {
        if (inputField != null) {
            if (inputField.trim().isEmpty()) {
                String message = String.format("Ошибка поле: %s, пустое или состоит из пробелов", nameField);
                log.error(message);
                throw new ObjectValidationException(message);
            }
        }
    }
}