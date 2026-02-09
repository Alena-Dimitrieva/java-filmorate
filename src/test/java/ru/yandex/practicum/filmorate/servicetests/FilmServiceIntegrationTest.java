package ru.yandex.practicum.filmorate.servicetests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmServiceIntegrationTest {

    private final JdbcTemplate jdbcTemplate;

    private FilmService filmService;

    @BeforeEach
    void setUp() {
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        GenreService genreService = new GenreService(new GenreDbStorage(jdbcTemplate));

        filmService = new FilmService(filmStorage, userStorage, genreService);

        // очистка на всякий случай
        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM genres");
        jdbcTemplate.update("DELETE FROM mpa");

        // справочники
        jdbcTemplate.update("""
                    INSERT INTO mpa (id, name) VALUES
                    (1,'G'),
                    (2,'PG'),
                    (3,'PG-13')
                """);

        jdbcTemplate.update("""
                    INSERT INTO genres (id, name) VALUES
                    (1,'Комедия'),
                    (2,'Драма')
                """);
    }

    @Test
    void create_shouldSaveFilmWithGenresAndMpa() {
        Film film = new Film();
        film.setName("Матрица");
        film.setDescription("Фантастика");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        film.setMpa(new Mpa(3, null));

        film.setGenres(new LinkedHashSet<>(Set.of(
                new Genre(1, null),
                new Genre(2, null)
        )));

        Film saved = filmService.create(film);

        assertThat(saved.getId()).isPositive();
        assertThat(saved.getMpa().getName()).isEqualTo("PG-13");
        assertThat(saved.getGenres())
                .extracting(Genre::getName)
                .containsExactly("Комедия", "Драма");
    }

    @Test
    void getById_shouldReturnFilmWithGenres() {
        Film film = new Film();
        film.setName("Начало");
        film.setDescription("Сон во сне");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);
        film.setMpa(new Mpa(2, null));
        film.setGenres(Set.of(new Genre(1, null)));

        Film saved = filmService.create(film);

        Film found = filmService.getById(saved.getId());

        assertThat(found.getName()).isEqualTo("Начало");
        assertThat(found.getMpa().getName()).isEqualTo("PG");
        assertThat(found.getGenres())
                .hasSize(1)
                .first()
                .extracting(Genre::getName)
                .isEqualTo("Комедия");
    }

    @Test
    void findAll_shouldReturnAllFilms() {
        Film film1 = new Film();
        film1.setName("Фильм 1");
        film1.setDescription("Описание");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(100);
        film1.setMpa(new Mpa(1, null));

        Film film2 = new Film();
        film2.setName("Фильм 2");
        film2.setDescription("Описание");
        film2.setReleaseDate(LocalDate.of(2005, 1, 1));
        film2.setDuration(120);
        film2.setMpa(new Mpa(2, null));

        filmService.create(film1);
        filmService.create(film2);

        List<Film> films = filmService.findAll();

        assertThat(films)
                .extracting(Film::getName)
                .containsExactlyInAnyOrder("Фильм 1", "Фильм 2");
    }

    @Test
    void update_shouldChangeFilmData() {
        Film film = new Film();
        film.setName("Original Name");
        film.setDescription("Original Desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        film.setMpa(new Mpa(1, "G"));
        film = filmService.create(film);

        film.setName("Updated Name");
        film.setDescription("Updated Desc");
        film.setDuration(110);
        Film updated = filmService.update(film);

        assertEquals("Updated Name", updated.getName());
        assertEquals("Updated Desc", updated.getDescription());
        assertEquals(110, updated.getDuration());
    }
}

