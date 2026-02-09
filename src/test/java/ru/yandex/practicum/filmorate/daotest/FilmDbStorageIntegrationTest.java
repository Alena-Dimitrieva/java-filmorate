package ru.yandex.practicum.filmorate.daotest;

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
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageIntegrationTest {

    private final JdbcTemplate jdbcTemplate;
    private FilmDbStorage filmDbStorage;

    @BeforeEach
    void setUp() {
        filmDbStorage = new FilmDbStorage(jdbcTemplate);

        // очистка таблиц
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM genres");
        jdbcTemplate.update("DELETE FROM mpa");
        jdbcTemplate.update("DELETE FROM users");

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
                    (2,'Драма'),
                    (3,'Боевик')
                """);

        // пользователи
        jdbcTemplate.update("""
                    INSERT INTO users (email, login, name, birthday) VALUES
                    ('user1@example.com','user1','Алена','1990-05-12'),
                    ('user2@example.com','user2','Иван','1985-03-23')
                """);
    }

    @Test
    void create_shouldAddFilm() {
        Film film = new Film();
        film.setName("Интерстеллар");
        film.setDescription("Космическое приключение");
        film.setReleaseDate(LocalDate.of(2014, 11, 7));
        film.setDuration(169);
        film.setMpa(new Mpa(3, "PG-13"));
        film.setGenres(new LinkedHashSet<>(List.of(new Genre(1, "Комедия"), new Genre(2, "Драма"))));

        Film created = filmDbStorage.create(film);

        assertThat(created.getId()).isGreaterThan(0);
        assertThat(created.getName()).isEqualTo(film.getName());
        assertThat(created.getGenres()).hasSize(2);
    }

    @Test
    void update_shouldModifyFilm() {
        Film film = new Film();
        film.setName("Фильм 1");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "G"));
        Film created = filmDbStorage.create(film);

        created.setName("Обновленное название");
        created.setGenres(new LinkedHashSet<>(List.of(new Genre(3, "Боевик"))));

        Film updated = filmDbStorage.update(created);
        assertThat(updated.getName()).isEqualTo("Обновленное название");
        assertThat(updated.getGenres()).hasSize(1);
    }

    @Test
    void getById_shouldReturnFilm() {
        Film film = new Film();
        film.setName("Фильм 2");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2021, 5, 5));
        film.setDuration(100);
        film.setMpa(new Mpa(2, "PG"));
        Film created = filmDbStorage.create(film);

        Film fetched = filmDbStorage.getById(created.getId()).orElse(null);
        assertThat(fetched).isNotNull();
        assertThat(fetched.getName()).isEqualTo("Фильм 2");
    }

    @Test
    void findAll_shouldReturnAllFilms() {
        Film film1 = new Film();
        film1.setName("Фильм A");
        film1.setDescription("Описание");
        film1.setReleaseDate(LocalDate.of(2010, 1, 1));
        film1.setDuration(90);
        film1.setMpa(new Mpa(1, "G"));
        filmDbStorage.create(film1);

        Film film2 = new Film();
        film2.setName("Фильм B");
        film2.setDescription("Описание");
        film2.setReleaseDate(LocalDate.of(2012, 2, 2));
        film2.setDuration(110);
        film2.setMpa(new Mpa(2, "PG"));
        filmDbStorage.create(film2);

        List<Film> all = filmDbStorage.findAll();
        assertThat(all).hasSize(2);
    }
}

