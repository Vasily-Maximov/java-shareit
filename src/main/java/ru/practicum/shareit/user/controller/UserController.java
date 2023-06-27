package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.check.CreateGroup;
import ru.practicum.shareit.check.UpdateGroup;
import ru.practicum.shareit.exeption.ErrorResponse;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User add(@Validated(CreateGroup.class) @RequestBody UserDto userDto) {
        log.info("Передан запрос на создание пользователя: {}", userDto);
        return userService.add(userDto);
    }

    @PatchMapping("/{userId}")
    public User update(@PathVariable Integer userId, @Validated(UpdateGroup.class) @RequestBody UserDto userDto) {
        log.info("Передан запрос на изменение пользователя по id:= {}, входными данными : {}", userId, userDto);
        return userService.update(userDto, userId);
    }

    @GetMapping("/{userId}")
    public User getById(@PathVariable Integer userId) {
        log.info("Передан запрос на получение пользователя по id:= {}", userId);
        return userService.getById(userId);
    }

    @GetMapping()
    public List<User> getAll() {
        return userService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delById(@PathVariable(value = "id") Integer idUser) {
        log.info("Запрос на удаление пользователя с id = {}", idUser);
        userService.delete(idUser);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameter(final NumberFormatException e) {
        String message = String.format("Некорректно передан параметр: %s", e.getMessage());
        log.info(message);
        return new ErrorResponse(message);
    }
}