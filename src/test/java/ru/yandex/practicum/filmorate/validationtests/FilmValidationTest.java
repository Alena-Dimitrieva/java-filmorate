package ru.yandex.practicum.filmorate.validationtests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.AbstractIntegrationTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidationTest extends AbstractIntegrationTest {

    private FilmService filmService;

    @BeforeEach
    void setup() {
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        InMemoryUserStorage userStorage = new InMemoryUserStorage(); // для лайков
        filmService = new FilmService(filmStorage, userStorage);
    }

    @Test
    void shouldNotCreateFilmWithEmptyName() {
        Film film = new Film();
        film.setName(""); // пустое имя — должно вызвать ValidationException
        film.setDescription("desc");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.now());

        assertThrows(ValidationException.class, () -> filmService.create(film));
    }

    @Test
    void shouldNotCreateFilmWithTooOldReleaseDate() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("desc");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1800, 1, 1)); // слишком старая дата

        assertThrows(ValidationException.class, () -> filmService.create(film));
    }
}
