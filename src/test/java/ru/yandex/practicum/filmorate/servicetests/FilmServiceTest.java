package ru.yandex.practicum.filmorate.servicetests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.AbstractIntegrationTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest extends AbstractIntegrationTest {

    private FilmService filmService;

    @BeforeEach
    void setup() {
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        InMemoryUserStorage userStorage = new InMemoryUserStorage(); // для лайков
        filmService = new FilmService(filmStorage, userStorage);
    }

    @Test
    void shouldCreateValidFilm() {
        Film film = new Film();
        film.setName("Interstellar");
        film.setDescription("Space opera");
        film.setDuration(140);
        film.setReleaseDate(LocalDate.of(2014, 1, 1));

        Film created = filmService.create(film);

        assertEquals("Interstellar", created.getName()); // проверка корректного создания
    }

    @Test
    void shouldNotCreateFilmWithTooOldReleaseDate() {
        Film film = new Film();
        film.setName("Ancient Movie");
        film.setDescription("Old one");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1800, 1, 1)); // слишком старая дата

        assertThrows(ValidationException.class, () -> filmService.create(film));
    }

    @Test
    void shouldNotUpdateNonExistingFilm() {
        Film film = new Film();
        film.setId(123); // несуществующий фильм
        film.setName("Test");
        film.setDescription("Desc");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));

        assertThrows(NoSuchElementException.class, () -> filmService.update(film));
    }
}
