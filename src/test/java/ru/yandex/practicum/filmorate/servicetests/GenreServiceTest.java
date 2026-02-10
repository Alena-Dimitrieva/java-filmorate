package ru.yandex.practicum.filmorate.servicetests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class GenreServiceTest {

    private GenreStorage genreStorage;
    private GenreService genreService;

    @BeforeEach
    void setUp() {
        genreStorage = Mockito.mock(GenreStorage.class);
        genreService = new GenreService(genreStorage);
    }

    @Test
    void findAll_shouldReturnAllGenres() {
        when(genreStorage.findAll()).thenReturn(List.of(
                new Genre(1, "Комедия"),
                new Genre(2, "Драма")
        ));

        List<Genre> genres = genreService.findAll();

        assertThat(genres).hasSize(2)
                .extracting(Genre::getName)
                .containsExactly("Комедия", "Драма");

        verify(genreStorage, times(1)).findAll();
    }

    @Test
    void getById_shouldReturnGenre_whenExists() {
        when(genreStorage.getById(1)).thenReturn(Optional.of(new Genre(1, "Комедия")));

        Genre genre = genreService.getById(1);

        assertThat(genre.getName()).isEqualTo("Комедия");
        verify(genreStorage, times(1)).getById(1);
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(genreStorage.getById(99)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> genreService.getById(99));

        verify(genreStorage, times(1)).getById(99);
    }
}



