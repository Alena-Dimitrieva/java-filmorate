package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель жанра.
 * Соответствует таблице genres (id, name).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    private int id;         // PK
    private String name;    // название жанра
}