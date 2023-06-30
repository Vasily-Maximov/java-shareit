package ru.practicum.shareit.user.repository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.abstarct.repository.AbstractRepository;
import ru.practicum.shareit.user.model.User;
import java.util.HashSet;
import java.util.Set;

@Repository
@Slf4j
@Getter
public class UserRepository extends AbstractRepository<User> {

    private final Set<String> emails = new HashSet<>();
}