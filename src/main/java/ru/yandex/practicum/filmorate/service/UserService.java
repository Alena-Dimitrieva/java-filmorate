package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User create(User user) {
        validate(user);
        applyDefaultName(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        // Проверка id убрана

        validate(user);
        applyDefaultName(user);

        userStorage.getById(user.getId())
                .orElseThrow(() -> new NoSuchElementException("Пользователь с таким id не найден"));

        return userStorage.update(user);
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User getById(int id) {
        // Проверка id убрана

        return userStorage.getById(id)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с таким id не найден"));
    }

    // добавление друга
    public void addFriend(int userId, int friendId) {
        userStorage.getById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
        userStorage.getById(friendId)
                .orElseThrow(() -> new NoSuchElementException("Друг не найден"));

        userStorage.addFriend(userId, friendId);
    }

    // удаление из друзей
    public void removeFriend(int userId, int friendId) {
        userStorage.getById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
        userStorage.getById(friendId)
                .orElseThrow(() -> new NoSuchElementException("Друг не найден"));

        userStorage.removeFriend(userId, friendId);
    }

    // список друзей
    public List<User> getFriends(int userId) {
        userStorage.getById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        return userStorage.getFriends(userId);
    }

    // общие друзья
    public List<User> getCommonFriends(int userId, int otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }

    // Расширенная валидация по ТЗ
    private void validate(User user) {
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

    private void applyDefaultName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}