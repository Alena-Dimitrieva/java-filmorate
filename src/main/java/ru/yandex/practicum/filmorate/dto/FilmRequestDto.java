package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmRequestDto {

    private Integer id;

    @NotBlank(message = "Название фильма обязательно")
    private String name;

    @Size(max = 200, message = "Описание фильма не должно превышать 200 символов")
    private String description;

    @NotNull(message = "Дата релиза обязательна")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительной")
    private int duration;

    @NotNull(message = "MPA рейтинг обязателен")
    private MpaRequestDto mpa;

    @NotNull
    private Set<GenreIdWrapper> genres = new LinkedHashSet<>();

    public Set<Integer> getGenreIds() {
        return genres.stream()
                .map(GenreIdWrapper::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GenreIdWrapper {
        private Integer id;
    }
}