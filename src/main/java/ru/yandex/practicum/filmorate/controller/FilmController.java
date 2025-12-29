package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
@Validated // Добавлено для валидации параметров (@PathVariable, @RequestParam)
public class FilmController {

    // Контроллер работает только с сервисом
    private final FilmService filmService;

    // Создание фильма
    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    // Обновление фильма
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    // Получение всех фильмов
    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    // Получение фильма по id (GET /films/{id})
    @GetMapping("/{id}")
    public Film getById(
            @PathVariable
            @Positive(message = "Id должен быть положительным") // Валидация id перенесена в контроллер
            int id
    ) {
        return filmService.getById(id);
    }

    // Пользователь ставит лайк фильму (PUT /films/{id}/like/{userId})
    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable("id")
            @Positive(message = "Id фильма должен быть положительным") // ИЗМЕНЕНО
            int id,

            @PathVariable("userId")
            @Positive(message = "Id пользователя должен быть положительным") // ИЗМЕНЕНО
            int userId
    ) {
        filmService.addLike(id, userId);
    }

    // Пользователь удаляет лайк (DELETE /films/{id}/like/{userId})
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @PathVariable("id")
            @Positive(message = "Id фильма должен быть положительным") // ИЗМЕНЕНО
            int id,

            @PathVariable("userId")
            @Positive(message = "Id пользователя должен быть положительным") // ИЗМЕНЕНО
            int userId
    ) {
        filmService.removeLike(id, userId);
    }

    // Получение популярных фильмов (GET /films/popular?count={count})
    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(defaultValue = "10")
            @Positive(message = "Параметр count должен быть больше 0") // ИЗМЕНЕНО: валидация count перенесена в контроллер
            int count
    ) {
        return filmService.getPopularFilms(count);
    }
}