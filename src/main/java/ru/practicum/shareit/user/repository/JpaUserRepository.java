package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;
import java.util.List;

public interface JpaUserRepository extends JpaRepository<User, Integer> {

    List<UserShort> findAllByEmailContainingIgnoreCase(String emailSearch);
}