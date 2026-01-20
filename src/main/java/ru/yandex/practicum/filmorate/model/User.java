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
 * Включает валидацию email, логина, даты рождения.
 */
@Data
public class User {

    private int id;

    @Email(message = "Некорректный e-mail формат")
    @NotBlank(message = "E-mail обязателен")
    private String email;

    /**
     * Логин не должен быть пустым и не может содержать пробелы.
     * Проверка на пробелы реализована вручную в сервисе.
     */
    @NotBlank(message = "Логин не может быть пустым")
    private String login;

    /**
     * Имя может быть пустым.
     * В сервисе оно заменится логином.
     */
    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    /**
     * Друзья пользователя и их статус дружбы.
     * Ключ — id друга, значение — статус: UNCONFIRMED / CONFIRMED
     */
    private Map<Integer, FriendshipStatus> friends = new HashMap<>();
}
