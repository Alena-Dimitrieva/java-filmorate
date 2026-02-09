package ru.yandex.practicum.filmorate.memorytests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class InMemoryFilmStorageTest {

    private InMemoryFilmStorage filmStorage;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
    }

    @Test
    void createAndGetById_shouldWork() {
        Film film = new Film();
        film.setName("Матрица");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);

        Film saved = filmStorage.create(film);
        assertThat(saved.getId()).isPositive();

        Film fetched = filmStorage.getById(saved.getId()).orElseThrow();
        assertThat(fetched.getName()).isEqualTo("Матрица");
    }

    @Test
    void update_shouldReplaceFilm() {
        Film film = new Film();
        film.setName("Original");
        film.setDuration(100);
        Film saved = filmStorage.create(film);

        saved.setName("Updated");
        saved.setDuration(120);
        Film updated = filmStorage.update(saved);

        assertThat(updated.getName()).isEqualTo("Updated");
        assertThat(updated.getDuration()).isEqualTo(120);
    }

    @Test
    void addAndRemoveLike_shouldWork() {
        Film film = new Film();
        film.setName("Film1");
        Film saved = filmStorage.create(film);

        filmStorage.addLike(saved.getId(), 1);
        filmStorage.addLike(saved.getId(), 2);

        Film fetched = filmStorage.getById(saved.getId()).orElseThrow();
        assertThat(fetched.getLikes()).hasSize(2).contains(1, 2);

        filmStorage.removeLike(saved.getId(), 1);
        assertThat(filmStorage.getById(saved.getId()).orElseThrow().getLikes()).containsExactly(2);
    }

    @Test
    void getPopularFilms_shouldReturnSorted() {
        Film f1 = new Film();
        f1.setName("A");
        Film f2 = new Film();
        f2.setName("B");
        Film f3 = new Film();
        f3.setName("C");

        f1 = filmStorage.create(f1);
        f2 = filmStorage.create(f2);
        f3 = filmStorage.create(f3);

        filmStorage.addLike(f2.getId(), 1);
        filmStorage.addLike(f2.getId(), 2);
        filmStorage.addLike(f3.getId(), 3);

        List<Film> popular = filmStorage.getPopularFilms(2);
        assertThat(popular).extracting(Film::getName).containsExactly("B", "C");
    }

    @Test
    void update_nonexistent_shouldThrow() {
        Film f = new Film();
        f.setId(999);
        assertThrows(NoSuchElementException.class, () -> filmStorage.update(f));
    }
}

