package ru.yandex.practicum.filmorate.controllertests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.AbstractIntegrationTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
class FilmControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // для сериализации в JSON

    @MockBean
    private FilmService filmService; // мок-сервис

    @Test
    void testCreateFilm() throws Exception {
        Film film = new Film();
        film.setId(1);
        film.setName("Titanic");
        film.setDescription("Love story");
        film.setReleaseDate(LocalDate.of(1997, 12, 19));
        film.setDuration(195);

        // Мокировка сервиса: при любом объекте Film возвращается наш объект
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
}