package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.Enum.Genre;
import ru.yandex.practicum.filmorate.Enum.MpaRating;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Модель фильма.
 * Содержит базовые поля и валидацию согласно требованиям ТЗ.
 */
@Data
public class Film {

    private int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    /**
     * Дата релиза фильма.
     * Дополнительная проверка на минимальную дату (28.12.1895)
     * выполняется в сервисе, потому что Bean Validation не умеет
     * проверять кастомные исторические даты без создания своего валидатора.
     */
    @NotNull(message = "Дата релиза обязательна")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительным числом")
    private int duration; // продолжительность в минутах

    /**
     * Идентификаторы пользователей, поставивших лайк фильму.
     */
    private Set<Integer> likes = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();//Жанры фильма
    private MpaRating mpaRating;//Рейтинг фильма по классификации MPA
}