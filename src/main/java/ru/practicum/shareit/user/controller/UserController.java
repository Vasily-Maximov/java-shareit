package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.practicum.shareit.check.CreateGroup;
import ru.practicum.shareit.check.UpdateGroup;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto add(@Validated(CreateGroup.class) @RequestBody UserDto userDto) {
        log.info("Передан запрос на создание пользователя: {}", userDto);
        return UserMapper.toUserDto(userService.add(userDto));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Integer userId, @Validated(UpdateGroup.class) @RequestBody UserDto userDto) {
        log.info("Передан запрос на изменение пользователя по id:= {}, входными данными : {}", userId, userDto);
        return UserMapper.toUserDto(userService.update(userDto, userId));
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Integer userId) {
        log.info("Передан запрос на получение пользователя по id:= {}", userId);
        return UserMapper.toUserDto(userService.getById(userId));
    }

    @GetMapping()
    public List<UserDto> getAll() {
        log.info("Передан запрос на получение всех пользователей");
        return userService.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void delById(@PathVariable(value = "id") Integer idUser) {
        log.info("Запрос на удаление пользователя с id = {}", idUser);
        userService.delete(idUser);
    }
}