package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    /**
     * Создание пользователя с заменой пустого имени на логин.
     */
    public User create(User user) {
        validate(user);
        applyDefaultName(user);
        return userStorage.create(user);
    }


    //Обновление существующего пользователя.
    public User update(User user) {

        if (user.getId() <= 0) {
            throw new ValidationException("Id должен быть положительным");
        }

        validate(user);
        applyDefaultName(user);

        // Проверка, что пользователь существует
        userStorage.getById(user.getId())
                .orElseThrow(() -> new ValidationException("Пользователь с таким id не найден"));

        return userStorage.update(user);
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

   //Проверки, которые нельзя выразить только аннотациями.
    private void validate(User user) {

        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен содержать пробелы");
        }

        if (user.getBirthday() != null &&
                user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    //Если имя пустое — подставляется логин.
    private void applyDefaultName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}