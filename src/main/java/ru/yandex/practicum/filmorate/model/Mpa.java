package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель рейтинга MPA.
 * Соответствует таблице mpa (id, name).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mpa {
    private int id;         // PK
    private String name;    // название рейтинга (например, "PG-13")
}