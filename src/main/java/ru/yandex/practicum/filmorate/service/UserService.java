package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    // добавлен явный конструктор с @Qualifier
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // Создание пользователя
    public User create(User user) {
        validateBusinessRules(user);
        applyDefaultName(user);
        return userStorage.create(user);
    }

    // Обновление пользователя
    public User update(User user) {
        validateBusinessRules(user);
        applyDefaultName(user);

        userStorage.getById(user.getId())
                .orElseThrow(() -> new NoSuchElementException("Пользователь с таким id не найден"));

        return userStorage.update(user);
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User getById(int id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с таким id не найден"));
    }

    // Добавление друга (одностороннее)
    public void addFriend(int userId, int friendId) {
        userStorage.getById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
        userStorage.getById(friendId)
                .orElseThrow(() -> new NoSuchElementException("Друг не найден"));

        userStorage.addFriend(userId, friendId);
    }

    // Удаление друга
    public void removeFriend(int userId, int friendId) {
        userStorage.getById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
        userStorage.getById(friendId)
                .orElseThrow(() -> new NoSuchElementException("Друг не найден"));

        userStorage.removeFriend(userId, friendId);
    }

    // Список друзей пользователя
    public List<User> getFriends(int userId) {
        userStorage.getById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        return userStorage.getFriends(userId);
    }

    // Общие друзья двух пользователей
    public List<User> getCommonFriends(int userId, int otherId) {
        // Проверка существования обоих пользователей
        userStorage.getById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
        userStorage.getById(otherId)
                .orElseThrow(() -> new NoSuchElementException("Друг не найден"));

        return userStorage.getCommonFriends(userId, otherId);
    }

    /**
     * Валидация бизнес-правил:
     * Логин не пустой и без пробелов
     * Email корректный
     * Дата рождения не в будущем
     */
    private void validateBusinessRules(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен содержать пробелы");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный e-mail формат");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    // Если name пустое, используется login
    private void applyDefaultName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}