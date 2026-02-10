package ru.yandex.practicum.filmorate.controllertests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.MpaController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WebMvcTest(controllers = MpaController.class)
class MpaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MpaService mpaService;

    @Test
    void getAll_shouldReturnListOfMpa() throws Exception {
        List<Mpa> mpaList = List.of(
                new Mpa(1, "G"),
                new Mpa(2, "PG")
        );

        when(mpaService.findAll()).thenReturn(mpaList);

        mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("G"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("PG"));
    }

    @Test
    void getById_shouldReturnMpa_whenExists() throws Exception {
        Mpa mpa = new Mpa(3, "PG-13");

        when(mpaService.getById(3)).thenReturn(mpa);

        mockMvc.perform(get("/mpa/{id}", 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("PG-13"));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        when(mpaService.getById(99))
                .thenThrow(new NoSuchElementException("MPA не найден"));

        mockMvc.perform(get("/mpa/{id}", 99))
                .andExpect(status().isNotFound());
    }
}

