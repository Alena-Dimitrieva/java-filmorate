package ru.yandex.practicum.filmorate.Enum;

import lombok.Getter;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

/**
 * Рейтинги по системе MPA
 * Добавлен @Getter от Lombok для доступа к id и name
 * Добавлен метод fromId(int id) для безопасного преобразования DTO -> Enum
 */
@Getter
public enum MpaRating {
    G(1, "G"),        // без возрастных ограничений
    PG(2, "PG"),      // детям рекомендуется смотреть с родителями
    PG_13(3, "PG-13"),// детям до 13 лет просмотр не желателен
    R(4, "R"),        // лицам до 17 лет просмотр возможен только в присутствии взрослого
    NC_17(5, "NC-17");// лицам до 18 лет просмотр запрещен

    private final int id;
    private final String nameWithDash;

    MpaRating(int id, String nameWithDash) {
        this.id = id;
        this.nameWithDash = nameWithDash;
    }

    /**
     * Получение MpaRating по id
     * Используется в Mapper при конвертации из MpaDto
     */
    public static MpaRating fromId(int id) {
        for (MpaRating rating : values()) {
            if (rating.id == id) return rating;
        }
        throw new NotFoundException("Неверный id MPA рейтинга: " + id);
    }
}