package ru.yandex.practicum.filmorate.validationtests;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.AbstractIntegrationTest;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidationTest extends AbstractIntegrationTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldNotValidateEmptyName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("desc");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.now());

        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    void shouldNotValidateTooLongDescription() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("A".repeat(300));
        film.setDuration(100);
        film.setReleaseDate(LocalDate.now());

        assertFalse(validator.validate(film).isEmpty());
    }
}