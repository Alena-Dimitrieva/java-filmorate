package ru.yandex.practicum.filmorate.validationtests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.AbstractIntegrationTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest extends AbstractIntegrationTest {

    private UserService userService;

    @BeforeEach
    void setup() {
        userService = new UserService(new InMemoryUserStorage());
    }

    @Test
    void shouldNotCreateUserWithEmptyLogin() {
        User user = new User();
        user.setEmail("a@a.com");
        user.setLogin(""); // пустой логин — ValidationException
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userService.create(user));
    }

    @Test
    void shouldNotCreateUserWithLoginWithSpaces() {
        User user = new User();
        user.setEmail("a@a.com");
        user.setLogin("bad login"); // пробелы в логине
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userService.create(user));
    }

    @Test
    void shouldNotCreateUserWithFutureBirthday() {
        User user = new User();
        user.setEmail("a@a.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().plusDays(1)); // дата в будущем

        assertThrows(ValidationException.class, () -> userService.create(user));
    }
}