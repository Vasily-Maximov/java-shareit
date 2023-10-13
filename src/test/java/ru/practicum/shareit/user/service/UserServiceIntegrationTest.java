package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.exeption.ObjectValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    private final UserDto userDtoRequest = new UserDto(null, "User", "user@yandex.ru");

    private final UserDto userDtoRequest2 = new UserDto(null, "User2", "user2@yandex.ru");

    private final UserDto userDtoRequest3 = new UserDto(null, "User2", "user@yandex.ru");


    @Test
    public void test01() {
        UserDto userDtoResponse = userService.add(userDtoRequest);
        assertNotNull(userDtoResponse.getId());
        assertEquals(1, userDtoResponse.getId());
        assertEquals(userDtoRequest.getName(), userDtoResponse.getName());
        assertEquals(userDtoRequest.getEmail(), userDtoResponse.getEmail());

        String errorMessage = String.format("Ошибка при создании/изменении пользователя неуникальный Email: %s",
                userDtoRequest3.getEmail());
        UserDto userDtoResponse2 = userService.add(userDtoRequest2);
        assertEquals(2, userDtoResponse2.getId());
        ObjectValidationException exception = Assertions.assertThrows(ObjectValidationException.class, () -> userService
                .update(userDtoRequest3, 2));
        assertEquals(errorMessage, exception.getMessage());

        List<UserDto> userDtoList = userService.getAll();
        assertEquals(2, userDtoList.size());

        userService.delete(2);
        assertThrows(ObjectNotFoundException.class, () -> userService.getById(2));
    }
}