package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.abstarct.AbstractService;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.exeption.ObjectValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImp extends AbstractService implements UserService {

    private final JpaUserRepository userRepository;

    private void checkDuplicateEmail(String newEmail) {
        if (!userRepository.findAllByEmailContainingIgnoreCase(newEmail).isEmpty()) {
            throw new ObjectValidationException(String.format("Ошибка при создании/изменении пользователя неуникальный Email: %s",
                    newEmail));
        }
    }

    @Override
    public UserDto add(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        userRepository.save(user);
        log.info("Создан пользователь: {}", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto, Integer userId) {
        User oldUser = UserMapper.toUser(getById(userId));
        String inputName = userDto.getName();
        super.checkField(inputName, "name");
        if (inputName == null) {
            userDto.setName(oldUser.getName());
        }
        String inputEmail = userDto.getEmail();
        super.checkField(inputEmail, "Email");
        if (inputEmail == null) {
            userDto.setEmail(oldUser.getEmail());
        } else {
            if (!oldUser.getEmail().equals(inputEmail)) {
                checkDuplicateEmail(inputEmail);
            }
        }
        User user = UserMapper.toUser(userDto);
        user.setId(oldUser.getId());
        userRepository.save(user);
        log.info("Выполнен запрос на изменение пользователя по id:= {}, входными данными : {}", userId, userDto);
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(Integer userId) {
        return UserMapper.toUserDto(userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException(String
                .format("Не найден пользователь по id: %d", userId))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        log.info("Выполнен запрос на получение пользователей:");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer userId) {
        userRepository.deleteById(userId);
        log.info("Выполнен запрос на удаление пользователя по id:= {}", userId);
    }
}