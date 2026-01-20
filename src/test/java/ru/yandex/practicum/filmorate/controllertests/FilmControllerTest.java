package ru.yandex.practicum.filmorate.controllertests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.AbstractIntegrationTest;
import ru.yandex.practicum.filmorate.Enum.MpaRating;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
class FilmControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FilmService filmService;

    @Test
    void testCreateFilm() throws Exception {
        Film film = new Film();
        film.setId(1);
        film.setName("Titanic");
        film.setDescription("Love story");
        film.setReleaseDate(LocalDate.of(1997, 12, 19));
        film.setDuration(195);

        film.setGenres(new HashSet<>()); // добавление пустого Set жанра
        film.setMpaRating(MpaRating.G); // добавление рейтинга по умолчанию

        when(filmService.create(any(Film.class))).thenReturn(film);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(film.getId()))
                .andExpect(jsonPath("$.name").value(film.getName()));

        verify(filmService, times(1)).create(any(Film.class));
    }

    @Test
    void testUpdateFilm() throws Exception {
        Film film = new Film();
        film.setId(1);
        film.setName("Titanic Updated");
        film.setDescription("Updated description");
        film.setReleaseDate(LocalDate.of(1997, 12, 19));
        film.setDuration(195);

        film.setGenres(new HashSet<>());// добавление пустых Set жанров
        film.setMpaRating(MpaRating.G); // добавление рейтинга по умолчанию

        when(filmService.update(any(Film.class))).thenReturn(film);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Titanic Updated"));

        verify(filmService, times(1)).update(any(Film.class));
    }

    @Test
    void testFindAllFilms() throws Exception {
        Film film = new Film();
        film.setId(1);
        film.setName("Titanic");
        film.setDescription("Love story");
        film.setReleaseDate(LocalDate.of(1997, 12, 19));
        film.setDuration(195);

        when(filmService.findAll()).thenReturn(List.of(film));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Titanic"));

        verify(filmService, times(1)).findAll();
    }

    //Новые тесты
    @Test
    void testAddLike() throws Exception {
        doNothing().when(filmService).addLike(1, 2);

        mockMvc.perform(put("/films/1/like/2"))
                .andExpect(status().isOk());

        verify(filmService, times(1)).addLike(1, 2);
    }

    @Test
    void testRemoveLike() throws Exception {
        doNothing().when(filmService).removeLike(1, 2);

        mockMvc.perform(delete("/films/1/like/2"))
                .andExpect(status().isOk());

        verify(filmService, times(1)).removeLike(1, 2);
    }

    @Test
    void testGetPopularFilms() throws Exception {
        Film film = new Film();
        film.setId(1);
        film.setName("Popular Film");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        when(filmService.getPopularFilms(5)).thenReturn(List.of(film));

        mockMvc.perform(get("/films/popular?count=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Popular Film"));

        verify(filmService, times(1)).getPopularFilms(5);
    }
}