package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class JpaUserRepositoryTest {

    @Autowired
    private JpaUserRepository userRepository;

    private List<UserShort> users;

    @Test
    public void findAllByEmailContainingIgnoreCaseTest01() {
        User user1 = new User(null,"User1", "user1@yandex.ru");
        User user2 = new User(null,"User2", "user2@mail.ru");
        userRepository.save(user1);
        userRepository.save(user2);
        users = userRepository.findAllByEmailContainingIgnoreCase("user1@yandex.ru");
        assertEquals(1, users.size());
        assertEquals(user1.getEmail(), users.get(0).getEmail());
        users = userRepository.findAllByEmailContainingIgnoreCase("user3@mail.ru");
        assertEquals(0, users.size());
    }
}