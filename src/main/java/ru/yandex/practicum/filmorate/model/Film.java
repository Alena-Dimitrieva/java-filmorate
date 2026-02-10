package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Модель фильма.
 * Поля соответствуют схеме БД (таблица films).
 */
@Data
public class Film {

    private int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name; // name NOT NULL

    @Size(max = 1024, message = "Описание не должно превышать 1024 символа")
    private String description; // description VARCHAR(1024)

    /**
     * Дата релиза фильма.
     * Валидация на минимальную дату (28.12.1895) должна выполняться в сервисном слое.
     */
    @NotNull(message = "Дата релиза обязательна")
    private LocalDate releaseDate; // release_date DATE

    @Positive(message = "Продолжительность должна быть положительным числом")
    private int duration; // duration INT NOT NULL

    /**
     * Содержит id пользователей, которые лайкнули фильм.
     */
    private Set<Integer> likes = new HashSet<>();

    /**
     * Жанры фильма.
     * порядок жанров должен сохраняться как в запросе.
     */
    private Set<Genre> genres = new LinkedHashSet<>();

    /**
     * Рейтинг MPA — объект, который соответствует записи в таблице mpa.
     */
    @NotNull(message = "Рейтинг MPA обязателен")
    private Mpa mpa;
}
