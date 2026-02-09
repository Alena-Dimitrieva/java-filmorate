package ru.yandex.practicum.filmorate.servicetests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    private FilmStorage filmStorage;

    @Mock
    private UserStorage userStorage;

    @Mock
    private GenreService genreService;

    @InjectMocks
    private FilmService filmService;

    private Film validFilm;

    @BeforeEach
    void setUp() {
        validFilm = new Film();
        validFilm.setName("Интерстеллар");
        validFilm.setDescription("Фантастика");
        validFilm.setReleaseDate(LocalDate.of(2014, 11, 7));
        validFilm.setDuration(169);
        validFilm.setMpa(new Mpa(3, null));

        validFilm.setGenres(new LinkedHashSet<>(Set.of(
                new Genre(1, null),
                new Genre(2, null)
        )));
    }

    @Test
    void create_shouldThrowValidationException_whenReleaseDateTooEarly() {
        validFilm.setReleaseDate(LocalDate.of(1800, 1, 1));

        assertThatThrownBy(() -> filmService.create(validFilm))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("1895");
    }

    @Test
    void create_shouldThrowValidationException_whenDurationInvalid() {
        validFilm.setDuration(0);

        assertThatThrownBy(() -> filmService.create(validFilm))
                .isInstanceOf(ValidationException.class);
    }
}
