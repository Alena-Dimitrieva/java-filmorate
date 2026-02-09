package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import ru.yandex.practicum.filmorate.Enum.FriendshipStatus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Модель пользователя.
 * Поля соответствуют схеме БД (таблица users).
 */
@Data
public class User {

    private int id; // PK, автоинкремент

    @Email(message = "Некорректный e-mail формат")
    @NotBlank(message = "E-mail обязателен")
    private String email; // email NOT NULL

    /**
     * Логин не должен быть пустым и не может содержать пробелы.
     * Проверка наличия пробелов выполняется в сервисе.
     */
    @NotBlank(message = "Логин не может быть пустым")
    private String login; // login NOT NULL

    /**
     * Имя пользователя. Может быть пустым; при создании сервиса
     * нужно заменять пустое имя значением login (логика в сервисе).
     */
    private String name; // name VARCHAR(255)

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday; // birthday DATE

    /**
     * Друзья и их статусы.
     * Ключ — id друга, значение — статус отношений (REQUESTED / CONFIRMED).
     * Таблица user_friends хранит пары (user_id, friend_id, status).
     */
    private Map<Integer, FriendshipStatus> friends = new HashMap<>();
}
