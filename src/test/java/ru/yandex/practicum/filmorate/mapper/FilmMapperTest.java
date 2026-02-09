package ru.yandex.practicum.filmorate.mapper;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.MpaRequestDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


class FilmMapperTest {

    @Test
    void fromRequestDto_shouldMapToFilm() {
        FilmRequestDto requestDto = new FilmRequestDto();
        requestDto.setId(1);
        requestDto.setName("Интерстеллар");
        requestDto.setDescription("Фантастика о космосе");
        requestDto.setReleaseDate(LocalDate.of(2014, 11, 7));
        requestDto.setDuration(169);

        // MPA — DTO, не model
        requestDto.setMpa(new MpaRequestDto(3));

        // жанры — тоже DTO
        requestDto.setGenres(Set.of(
                new FilmRequestDto.GenreIdWrapper(1),
                new FilmRequestDto.GenreIdWrapper(2)
        ));

        Film film = FilmMapper.fromRequestDto(requestDto);

        assertThat(film).isNotNull();
        assertThat(film.getId()).isEqualTo(1);
        assertThat(film.getName()).isEqualTo("Интерстеллар");
        assertThat(film.getDescription()).isEqualTo("Фантастика о космосе");
        assertThat(film.getReleaseDate()).isEqualTo(LocalDate.of(2014, 11, 7));
        assertThat(film.getDuration()).isEqualTo(169);

        assertThat(film.getMpa()).isNotNull();
        assertThat(film.getMpa().getId()).isEqualTo(3);

        assertThat(film.getGenres())
                .map(Genre::getId)
                .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void toResponseDto_shouldMapToDto() {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genres.add(new Genre(1, "Комедия"));
        genres.add(new Genre(2, "Драма"));

        Film film = new Film();
        film.setId(1);
        film.setName("Интерстеллар");
        film.setDescription("Фантастика о космосе");
        film.setReleaseDate(LocalDate.of(2014, 11, 7));
        film.setDuration(169);
        film.setMpa(new Mpa(3, "PG-13"));
        film.setGenres(genres);

        FilmResponseDto responseDto = FilmMapper.toResponseDto(film);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(1);
        assertThat(responseDto.getName()).isEqualTo("Интерстеллар");
        assertThat(responseDto.getDescription()).isEqualTo("Фантастика о космосе");
        assertThat(responseDto.getReleaseDate()).isEqualTo(LocalDate.of(2014, 11, 7));
        assertThat(responseDto.getDuration()).isEqualTo(169);

        assertThat(responseDto.getMpa()).isNotNull();
        assertThat(responseDto.getMpa().getId()).isEqualTo(3);

        assertThat(responseDto.getGenres())
                .extracting("id")
                .containsExactly(1, 2);
    }
}
