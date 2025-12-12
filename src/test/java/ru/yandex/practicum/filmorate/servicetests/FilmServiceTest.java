package ru.yandex.practicum.filmorate.servicetests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.AbstractIntegrationTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest extends AbstractIntegrationTest {

    private FilmService filmService;

    @BeforeEach
    void setup() {
        filmService = new FilmService(new InMemoryFilmStorage());
    }

    @Test
    void shouldCreateValidFilm() {
        Film film = new Film();
        film.setName("Interstellar");
        film.setDescription("Space opera");
        film.setDuration(140);
        film.setReleaseDate(LocalDate.of(2014, 1, 1));

        Film created = filmService.create(film);

        assertEquals("Interstellar", created.getName());
    }

    @Test
    void shouldNotCreateFilmWithTooOldReleaseDate() {
        Film film = new Film();
        film.setName("Ancient Movie");
        film.setDescription("Old one");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1800, 1, 1));

        assertThrows(ValidationException.class, () -> filmService.create(film));
    }

    @Test
    void shouldNotUpdateNonExistingFilm() {
        Film film = new Film();
        film.setId(123);
        film.setName("Test");
        film.setDescription("Desc");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> filmService.update(film));
    }
}