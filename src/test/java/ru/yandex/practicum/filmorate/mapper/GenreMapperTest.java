package ru.yandex.practicum.filmorate.mapper;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


class GenreMapperTest {

    @Test
    void toDto_shouldMapGenreToDto() {
        Genre genre = new Genre(1, "Комедия");

        GenreDto dto = GenreMapper.toDto(genre);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Комедия");
    }

    @Test
    void fromDto_shouldMapDtoToGenre() {
        GenreDto dto = new GenreDto(2, "Драма");

        Genre genre = GenreMapper.fromDto(dto);

        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(2);
        assertThat(genre.getName()).isEqualTo("Драма");
    }

    @Test
    void toDtoSet_shouldMapGenreSetToDtoSet() {
        Set<Genre> genres = Set.of(
                new Genre(1, "Комедия"),
                new Genre(2, "Драма")
        );

        Set<GenreDto> dtoSet = GenreMapper.toDtoSet(genres);

        assertThat(dtoSet).hasSize(2);
        assertThat(dtoSet)
                .extracting("id")
                .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void fromDtoSet_shouldMapDtoSetToGenreSet() {
        Set<GenreDto> dtos = Set.of(
                new GenreDto(3, "Триллер"),
                new GenreDto(4, "Ужасы")
        );

        Set<Genre> genres = GenreMapper.fromDtoSet(dtos);

        assertThat(genres).hasSize(2);
        assertThat(genres)
                .extracting("id")
                .containsExactlyInAnyOrder(3, 4);
    }

    @Test
    void toDto_shouldReturnNull_whenGenreIsNull() {
        assertThat(GenreMapper.toDto(null)).isNull();
    }

    @Test
    void fromDto_shouldReturnNull_whenDtoIsNull() {
        assertThat(GenreMapper.fromDto(null)).isNull();
    }

    @Test
    void toDtoSet_shouldReturnEmptySet_whenInputIsNullOrEmpty() {
        assertThat(GenreMapper.toDtoSet(null)).isEmpty();
        assertThat(GenreMapper.toDtoSet(Set.of())).isEmpty();
    }

    @Test
    void fromDtoSet_shouldReturnEmptySet_whenInputIsNullOrEmpty() {
        assertThat(GenreMapper.fromDtoSet(null)).isEmpty();
        assertThat(GenreMapper.fromDtoSet(Set.of())).isEmpty();
    }
}
