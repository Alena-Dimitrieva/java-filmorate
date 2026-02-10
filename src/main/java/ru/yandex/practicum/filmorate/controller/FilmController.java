package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
@Validated
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public FilmResponseDto create(@Valid @RequestBody FilmRequestDto filmRequestDto) {
        Film film = FilmMapper.fromRequestDto(filmRequestDto);
        Film saved = filmService.create(film);
        return FilmMapper.toResponseDto(saved);
    }

    @PutMapping
    public FilmResponseDto update(@Valid @RequestBody FilmRequestDto filmRequestDto) {
        Film film = FilmMapper.fromRequestDto(filmRequestDto);
        Film updated = filmService.update(film);
        return FilmMapper.toResponseDto(updated);
    }

    @GetMapping
    public List<FilmResponseDto> findAll() {
        return filmService.findAll().stream()
                .map(FilmMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public FilmResponseDto getById(@PathVariable @Positive(message = "Id должен быть положительным") int id) {
        Film film = filmService.getById(id);
        return FilmMapper.toResponseDto(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") @Positive int id,
                        @PathVariable("userId") @Positive int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") @Positive int id,
                           @PathVariable("userId") @Positive int userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<FilmResponseDto> getPopularFilms(@RequestParam(defaultValue = "10")
                                                 @Positive int count) {
        return filmService.getPopularFilms(count).stream()
                .map(FilmMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
