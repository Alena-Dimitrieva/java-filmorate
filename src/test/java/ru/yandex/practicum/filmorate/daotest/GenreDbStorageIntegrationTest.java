package ru.yandex.practicum.filmorate.daotest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GenreDbStorageIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private GenreDbStorage genreDbStorage;

    @BeforeEach
    void setUp() {
        genreDbStorage = new GenreDbStorage(jdbcTemplate);

        // Очистка зависимых таблиц
        jdbcTemplate.update("DELETE FROM film_genres");

        // Справочник жанров - можно вставить нужные записи только если их нет
        jdbcTemplate.update("""
                        MERGE INTO genres (id, name) KEY(id) VALUES
                        (1, 'Комедия'),
                        (2, 'Драма'),
                        (3, 'Мультфильм')
                """);
    }

    @Test
    void findAll_shouldReturnAllGenres() {
        List<Genre> genres = genreDbStorage.findAll();

        assertTrue(genres.stream().anyMatch(g -> g.getId() == 1 && g.getName().equals("Комедия")));
        assertTrue(genres.stream().anyMatch(g -> g.getId() == 2 && g.getName().equals("Драма")));
        assertTrue(genres.stream().anyMatch(g -> g.getId() == 3 && g.getName().equals("Мультфильм")));
    }

    @Test
    void getById_shouldReturnGenre_whenExists() {
        Optional<Genre> genre = genreDbStorage.getById(2);

        assertThat(genre).isPresent();
        assertThat(genre.get().getName()).isEqualTo("Драма");
    }

    @Test
    void getById_shouldReturnEmpty_whenNotFound() {
        Optional<Genre> genre = genreDbStorage.getById(999);

        assertThat(genre).isEmpty();
    }
}
