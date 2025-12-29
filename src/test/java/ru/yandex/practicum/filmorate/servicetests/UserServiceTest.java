package ru.yandex.practicum.filmorate.servicetests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.AbstractIntegrationTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest extends AbstractIntegrationTest {

    private UserService userService;

    @BeforeEach
    void setup() {
        userService = new UserService(new InMemoryUserStorage());
    }

    @Test
    void shouldCreateUserAndSetNameAsLoginWhenNameIsEmpty() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("tester");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.setName(""); // имя пустое — должно подставиться из логина

        User created = userService.create(user);

        assertEquals("tester", created.getName()); // проверка подстановки имени
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
        user.setEmail("test@mail.com");
        user.setLogin("bad login"); // пробелы в логине
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userService.create(user));
    }

    @Test
    void shouldNotUpdateNonExistingUser() {
        User user = new User();
        user.setId(999); // несуществующий пользователь
        user.setEmail("a@a.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(NoSuchElementException.class, () -> userService.update(user));
    }
}