package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
     * Идентификаторы друзей пользователя.
     */
    private Set<Integer> friends = new HashSet<>();
}
