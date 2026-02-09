package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@Slf4j
@Validated
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public List<GenreDto> getAll() {
        List<Genre> genres = genreService.findAll();
        return genres.stream()
                .map(GenreMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public GenreDto getById(@PathVariable @Positive(message = "Id жанра должен быть положительным") int id) {
        Genre genre = genreService.getById(id);
        if (genre == null) {
            throw new ResponseStatusException(NOT_FOUND, "Жанр не найден");
        }
        return GenreMapper.toDto(genre);
    }
}