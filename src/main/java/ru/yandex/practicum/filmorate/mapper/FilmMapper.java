package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.Enum.MpaRating;
import ru.yandex.practicum.filmorate.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FilmMapper {

    public static Film fromRequestDto(FilmRequestDto requestDto) {
        if (requestDto == null) return null;

        Film film = new Film();
        film.setId(requestDto.getId() != null ? requestDto.getId() : 0);
        film.setName(requestDto.getName());
        film.setDescription(requestDto.getDescription());
        film.setReleaseDate(requestDto.getReleaseDate());
        film.setDuration(requestDto.getDuration());

        // Конвертация через Enum
        if (requestDto.getMpa() != null) {
            MpaRating rating = MpaRating.fromId(requestDto.getMpa().getId());
            film.setMpa(new Mpa(rating.getId(), rating.getNameWithDash()));
        }

        // Преобразование жанров через GenreMapper и новый FilmRequestDto
        if (requestDto.getGenres() != null && !requestDto.getGenres().isEmpty()) {
            Set<Genre> genres = requestDto.getGenreIds().stream()
                    .map(id -> new Genre(id, null))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            film.setGenres(genres);
        } else {
            film.setGenres(Set.of());
        }

        return film;
    }

    public static FilmResponseDto toResponseDto(Film film) {
        if (film == null) return null;

        FilmResponseDto responseDto = new FilmResponseDto();
        responseDto.setId(film.getId());
        responseDto.setName(film.getName());
        responseDto.setDescription(film.getDescription());
        responseDto.setReleaseDate(film.getReleaseDate());
        responseDto.setDuration(film.getDuration());

        // MPA уже полный объект
        responseDto.setMpa(film.getMpa());

        // Жанры через GenreMapper с сохранением порядка
        responseDto.setGenres(film.getGenres() != null
                ? film.getGenres().stream()
                .map(GenreMapper::toDto)
                .collect(Collectors.toCollection(LinkedHashSet::new))
                : Set.of());

        return responseDto;
    }
}


