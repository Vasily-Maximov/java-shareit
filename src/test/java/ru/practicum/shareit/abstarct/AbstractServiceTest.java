package ru.practicum.shareit.abstarct;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.practicum.shareit.exeption.ObjectValidationException;
import ru.practicum.shareit.user.repository.JpaUserRepository;
import ru.practicum.shareit.user.service.UserServiceImp;

import static org.junit.jupiter.api.Assertions.*;

class AbstractServiceTest {

    private AbstractService userService;

    @Mock
    private JpaUserRepository userRepository;

    @BeforeEach
    public void beforeEach() {
        userService = new UserServiceImp(userRepository);
    }

    @Test
    public void checkField() {
        String errorMessage = String.format("Ошибка поле: %s, пустое или состоит из пробелов", "name");
        ObjectValidationException exception = Assertions.assertThrows(ObjectValidationException.class, () -> userService
                .checkField("  ", "name"));
        assertEquals(errorMessage, exception.getMessage());

        exception = Assertions.assertThrows(ObjectValidationException.class, () -> userService
                .checkField("", "name"));
        assertEquals(errorMessage, exception.getMessage());
    }
}