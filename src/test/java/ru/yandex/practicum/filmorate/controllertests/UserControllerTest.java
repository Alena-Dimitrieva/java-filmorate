package ru.yandex.practicum.filmorate.controllertests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.AbstractIntegrationTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void testCreateUser() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setLogin("tester");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.create(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("tester"));

        verify(userService, times(1)).create(any(User.class));
    }

    @Test
    void testUpdateUser() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setLogin("testerUpdated");
        user.setName("Test Updated");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.update(any(User.class))).thenReturn(user);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("testerUpdated"));

        verify(userService, times(1)).update(any(User.class));
    }

    @Test
    void testFindAllUsers() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setLogin("tester");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.findAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value("tester"));

        verify(userService, times(1)).findAll();
    }
}