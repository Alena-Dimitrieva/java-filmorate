package ru.yandex.practicum.filmorate.controllertests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.filmorate.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.dto.MpaRequestDto;

import java.time.LocalDate;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
class FilmControllerTest {

    @LocalServerPort
    private int port;

    private String baseUrl;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/films";
        restTemplate = new RestTemplate();
    }

    //создание фильма
    @Test
    void createFilm_success() {
        FilmRequestDto request = new FilmRequestDto();
        request.setName("Тестовый фильм");
        request.setDescription("Описание фильма");
        request.setReleaseDate(LocalDate.of(2020, 1, 1));
        request.setDuration(120);
        request.setMpa(new MpaRequestDto(3));
        request.setGenres(new HashSet<>());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<FilmRequestDto> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Тестовый фильм");
    }

    //ниже 2 теста на валидация
    @Test
    void createFilm_shouldFailValidation_emptyName() {
        FilmRequestDto request = new FilmRequestDto();
        request.setName(""); // пустое имя
        request.setDescription("Описание фильма");
        request.setReleaseDate(LocalDate.of(2020, 1, 1));
        request.setDuration(120);
        request.setMpa(new MpaRequestDto(3));
        request.setGenres(new HashSet<>());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FilmRequestDto> entity = new HttpEntity<>(request, headers);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> restTemplate.postForEntity(baseUrl, entity, String.class));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getResponseBodyAsString()).contains("Название фильма обязательно");
    }

    @Test
    void createFilm_shouldFailValidation_negativeDuration() {
        FilmRequestDto request = new FilmRequestDto();
        request.setName("Фильм");
        request.setDescription("Описание фильма");
        request.setReleaseDate(LocalDate.of(2020, 1, 1));
        request.setDuration(-10); // отрицательная длительность
        request.setMpa(new MpaRequestDto(3));
        request.setGenres(new HashSet<>());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FilmRequestDto> entity = new HttpEntity<>(request, headers);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> restTemplate.postForEntity(baseUrl, entity, String.class));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getResponseBodyAsString()).contains("Продолжительность должна быть положительной");
    }
}
