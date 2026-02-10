package ru.yandex.practicum.filmorate.Enum;

import lombok.Getter;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

/**
 * Возможные жанры фильмов
 * Добавлено поле name для удобного отображения
 * Добавлен @Getter от Lombok
 */
@Getter
public enum Genre {
    COMEDY(1, "Комедия"),
    DRAMA(2, "Драма"),
    CARTOON(3, "Мультфильм"),
    THRILLER(4, "Триллер"),
    DOCUMENTARY(5, "Документальный"),
    ACTION(6, "Боевик");

    private final int id;
    private final String name;

    Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Получение Genre по id
    public static Genre fromId(int id) {
        for (Genre genre : values()) {
            if (genre.id == id) return genre;
        }
        throw new NotFoundException("Неверный id жанра: " + id);
    }
}