package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.abstarct.AbstractService;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.exeption.ObjectValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UserServiceImpTest extends AbstractService {

    private UserService userService;

    private final UserDto userDtoRequest = new UserDto(null, "User", "user@yandex.ru");

    private final UserDto userDtoRequestNull = new UserDto(null, null, null);

    @Mock
    private JpaUserRepository userRepository;

    @BeforeEach
    public void beforeEach() {
        userService = new UserServiceImp(userRepository);
    }

    @Test
    public void addTest01() {
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                        .thenAnswer(invocationOnMock -> {
                            User user = invocationOnMock.getArgument(0, User.class);
                            user.setId(1);
                            return user;
                        });
        UserDto userDtoResponse = userService.add(userDtoRequest);
        assertEquals(1, userDtoResponse.getId());
        assertEquals(userDtoRequest.getName(), userDtoResponse.getName());
        assertEquals(userDtoRequest.getEmail(), userDtoResponse.getEmail());
        verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    public void updateTest02() {
        User user = new User(1, "UserOld", "userOld@yandex.ru");
        Mockito.when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        UserDto userDtoResponse = userService.update(userDtoRequest, 1);
        assertEquals(1, userDtoResponse.getId());
        assertEquals(userDtoRequest.getName(), userDtoResponse.getName());
        assertEquals(userDtoRequest.getEmail(), userDtoResponse.getEmail());
        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    public void updateUserNotFoundTest03() {
        String errorMessage = String.format("Не найден пользователь по id: %d", 10);
        Mockito.when(userRepository.findById(Mockito.anyInt()))
                .thenThrow(new ObjectNotFoundException(errorMessage));
        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class, () -> userService
                .update(userDtoRequest, 10));
        assertEquals(errorMessage, exception.getMessage());
        verify(userRepository, never()).save(Mockito.any(User.class));
    }

    @Test
    public void updateDuplicateEmailTest04() {
        String errorMessage = String.format("Ошибка при создании/изменении пользователя неуникальный Email: %s",
                "newEmail");
        User user = new User(1, "UserOld", "userOld@yandex.ru");
        Mockito.when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(userRepository.findAllByEmailContainingIgnoreCase(Mockito.anyString()))
                .thenThrow(new ObjectValidationException(errorMessage));
        ObjectValidationException exception = Assertions.assertThrows(ObjectValidationException.class, () -> userService
                .update(userDtoRequest, 10));
        assertEquals(errorMessage, exception.getMessage());
        verify(userRepository, never()).save(Mockito.any(User.class));
    }

    @Test
    public void getByIdTest05() {
        User user = new User(1, "User", "user@yandex.ru");
        Mockito.when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        UserDto userDto = userService.getById(1);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getName(), userDto.getName());
    }

    @Test
    public void getByIdExceptionTest06() {
        String errorMessage = String.format("Не найден пользователь по id: %d", 10);
        Mockito.when(userRepository.findById(Mockito.anyInt()))
                .thenThrow(new ObjectNotFoundException(errorMessage));
        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class, () -> userService
                .getById(10));
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    public void getAllEmptyListTest07() {
        Mockito.when(userRepository.findAll())
                .thenReturn(new ArrayList<>());
        assertEquals(userService.getAll(), new ArrayList<>());
    }

    @Test
    public void getAll08() {
        User user = new User(1, "User", "user@yandex.ru");
        User user2 = new User(2, "User2", "user2@yandex.ru");
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user, user2));
        List<UserDto> users = userService.getAll();
        assertEquals(2, users.size());
        assertEquals(user.getId(), users.get(0).getId());
        assertEquals(user.getEmail(), users.get(0).getEmail());
        assertEquals(user2.getId(), users.get(1).getId());
        assertEquals(user2.getName(), users.get(1).getName());
    }

    @Test
    void deleteTest09() {
        userService.delete(1);
        Mockito.verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    public void updateTest10() {
        User user = new User(1, "UserOld", "userOld@yandex.ru");
        Mockito.when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        UserDto userDtoResponse = userService.update(userDtoRequestNull, 1);
        assertEquals(1, userDtoResponse.getId());
        assertEquals(user.getName(), userDtoResponse.getName());
        assertEquals(user.getEmail(), userDtoResponse.getEmail());
        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }
}