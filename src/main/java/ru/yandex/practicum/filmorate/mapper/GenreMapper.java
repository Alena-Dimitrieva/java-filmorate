package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;
import java.util.stream.Collectors;

public class GenreMapper {

    // Преобразование Genre -> GenreDto
    public static GenreDto toDto(Genre genre) {
        if (genre == null) return null;
        return new GenreDto(genre.getId(), genre.getName());
    }

    // Преобразование GenreDto -> Genre
    public static Genre fromDto(GenreDto dto) {
        if (dto == null) return null;
        return new Genre(dto.getId(), dto.getName());
    }

    // Утилиты для работы с коллекциями

    public static Set<GenreDto> toDtoSet(Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) return Set.of();
        return genres.stream().map(GenreMapper::toDto).collect(Collectors.toSet());
    }

    public static Set<Genre> fromDtoSet(Set<GenreDto> dtos) {
        if (dtos == null || dtos.isEmpty()) return Set.of();
        return dtos.stream().map(GenreMapper::fromDto).collect(Collectors.toSet());
    }
}
