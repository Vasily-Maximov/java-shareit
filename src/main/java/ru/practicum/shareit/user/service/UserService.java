package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

public interface UserService {

    UserDto add(UserDto userDto);

    UserDto getById(Integer userId);

    List<UserDto> getAll();

    UserDto update(UserDto userDto, Integer userId);

    void delete(Integer userId);
}