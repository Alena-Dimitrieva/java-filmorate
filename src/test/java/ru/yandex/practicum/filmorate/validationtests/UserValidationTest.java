package ru.yandex.practicum.filmorate.validationtests;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.AbstractIntegrationTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest extends AbstractIntegrationTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldNotValidateWrongEmail() {
        User user = new User();
        user.setEmail("wrong_email");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    void shouldNotValidateEmptyLogin() {
        User user = new User();
        user.setEmail("a@a.com");
        user.setLogin("");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertFalse(validator.validate(user).isEmpty());
    }
}