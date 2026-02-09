package ru.yandex.practicum.filmorate.servicetests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserStorage userStorage;
    private UserService userService;

    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    @BeforeEach
    void setup() {
        userStorage = mock(UserStorage.class);
        userService = new UserService(userStorage);
    }

    @Test
    void createUser_shouldApplyDefaultName_whenNameIsEmpty() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("loginOnly");
        user.setName("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setEmail(user.getEmail());
        savedUser.setLogin(user.getLogin());
        savedUser.setName(user.getLogin());
        savedUser.setBirthday(user.getBirthday());

        when(userStorage.create(any(User.class))).thenReturn(savedUser);

        User result = userService.create(user);

        assertThat(result.getName()).isEqualTo("loginOnly");
        verify(userStorage, times(1)).create(any(User.class));
    }

    @Test
    void createUser_shouldThrowValidationException_onInvalidEmail() {
        User user = new User();
        user.setEmail("invalidEmail");
        user.setLogin("login1");
        user.setName("Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThatThrownBy(() -> userService.create(user))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Некорректный e-mail формат");

        verify(userStorage, never()).create(any());
    }

    @Test
    void getById_shouldCallStorageAndReturnUser() {
        User user = new User();
        user.setId(1);
        user.setLogin("login");
        user.setEmail("email@test.com");

        when(userStorage.getById(1)).thenReturn(Optional.of(user));

        User result = userService.getById(1);

        assertThat(result).isEqualTo(user);
        verify(userStorage, times(1)).getById(1);
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        when(userStorage.getById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(999))
                .hasMessageContaining("Пользователь с таким id не найден");

        verify(userStorage, times(1)).getById(999);
    }
}

