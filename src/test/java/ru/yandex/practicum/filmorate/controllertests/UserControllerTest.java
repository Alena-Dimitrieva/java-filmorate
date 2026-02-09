package ru.yandex.practicum.filmorate.controllertests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.dto.UserDto;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto user1;
    private UserDto user2;

    @BeforeEach
    void setUp() {
        // тестовые пользователи
        user1 = new UserDto(0, "alice@example.com", "alice", "Alice", LocalDate.of(1990, 1, 1));
        user2 = new UserDto(0, "bob@example.com", "bob", "Bob", LocalDate.of(1992, 2, 2));
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        ResultActions result = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.login").value("alice"))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void addFriendAndGetFriends_shouldReturnFriendList() throws Exception {
        // Сначала создаём пользователей
        UserDto created1 = objectMapper.readValue(mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andReturn().getResponse().getContentAsString(), UserDto.class);

        UserDto created2 = objectMapper.readValue(mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andReturn().getResponse().getContentAsString(), UserDto.class);

        // Добавляем друг друга
        mockMvc.perform(put("/users/{id}/friends/{friendId}", created1.getId(), created2.getId()))
                .andExpect(status().isOk());

        // Проверяем список друзей пользователя 1
        mockMvc.perform(get("/users/{id}/friends", created1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(created2.getId()))
                .andExpect(jsonPath("$[0].login").value("bob"));
    }

    @Test
    void getCommonFriends_shouldReturnEmptyListIfNoCommonFriends() throws Exception {
        // Создаём трёх пользователей
        UserDto u1 = objectMapper.readValue(mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andReturn().getResponse().getContentAsString(), UserDto.class);

        UserDto u2 = objectMapper.readValue(mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andReturn().getResponse().getContentAsString(), UserDto.class);

        UserDto u3 = new UserDto(0, "charlie@example.com", "charlie", "Charlie", LocalDate.of(1995, 3, 3));
        UserDto u3Created = objectMapper.readValue(mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(u3)))
                .andReturn().getResponse().getContentAsString(), UserDto.class);

        // Добавляем друзей: u1 -> u3, u2 -> u3
        mockMvc.perform(put("/users/{id}/friends/{friendId}", u1.getId(), u3Created.getId()))
                .andExpect(status().isOk());
        mockMvc.perform(put("/users/{id}/friends/{friendId}", u2.getId(), u3Created.getId()))
                .andExpect(status().isOk());

        // Проверяем общих друзей u1 и u2
        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", u1.getId(), u2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(u3Created.getId()));
    }

    @Test
    void createUser_withInvalidEmail_shouldReturnBadRequest() throws Exception {
        user1.setEmail("invalid-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isBadRequest());
    }
}

