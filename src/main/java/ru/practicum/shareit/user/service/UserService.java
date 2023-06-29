package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstarct.repository.AbstractRepository;
import ru.practicum.shareit.abstarct.service.AbstractService;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.exeption.ObjectValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.List;

@Service
@Slf4j
public class UserService extends AbstractService<User> {

    private final UserRepository userRepository;

    @Autowired
    public UserService(AbstractRepository<User> repository, UserRepository userRepository) {
        super(repository);
        this.userRepository = userRepository;
    }

    private void checkDuplicateEmail(String newEmail) {
        if (userRepository.getEmails().contains(newEmail)) {
            throw new ObjectValidationException(String.format("Ошибка при создании/изменении пользователя неуникальный Email: %s",
                    newEmail));
        }
    }

    public User add(UserDto userDto) {
        checkDuplicateEmail(userDto.getEmail());
        User user = UserMapper.toUser(userDto);
        super.add(user);
        userRepository.getEmails().add(user.getEmail());
        log.info("Создан пользователь: {}", user);
        return user;
    }

    public User update(UserDto userDto, Integer userId) {
        User oldUser = getById(userId);
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
                userRepository.getEmails().remove(oldUser.getEmail());
                userRepository.getEmails().add(inputEmail);
            }
        }
        User user = UserMapper.toUser(userDto);
        user.setId(oldUser.getId());
        super.update(user);
        log.info("Выполнен запрос на изменение пользователя по id:= {}, входными данными : {}", userId, userDto);
        return user;
    }

    public User getById(Integer userId) {
        User user = super.getById(userId);
        if (user == null) {
            String message = String.format("Не найден пользователь по id: %d", userId);
            log.info(message);
            throw new ObjectNotFoundException(message);
        }
        return user;
    }

    public List<User> getAll() {
        log.info("Выполнен запрос на получение пользователей:");
        return super.getAll();
    }

    public void delete(Integer userId) {
        userRepository.getEmails().remove(getById(userId).getEmail());
        super.delete(userId);
        log.info("Выполнен запрос на удаление пользователя по id:= {}", userId);
    }
}