package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.abstarct.repository.AbstractRepository;
import ru.practicum.shareit.user.model.User;

@Repository
@Slf4j
public class UserRepository extends AbstractRepository<User> {
}