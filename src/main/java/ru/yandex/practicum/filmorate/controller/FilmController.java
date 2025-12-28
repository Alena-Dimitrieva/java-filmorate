package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    // Контроллер работает только с сервисом, а не с хранилищем
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
    public Film getById(@PathVariable int id) {
        return filmService.getById(id);
    }

    // Пользователь ставит лайк фильму (PUT /films/{id}/like/{userId})
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        filmService.addLike(id, userId);
    }

    // Пользователь удаляет лайк (DELETE /films/{id}/like/{userId})
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        filmService.removeLike(id, userId);
    }

    // Получение популярных фильмов (GET /films/popular?count={count})
    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }
}