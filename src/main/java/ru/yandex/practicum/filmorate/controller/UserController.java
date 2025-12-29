package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
@Validated // Добавлено для валидации параметров
public class UserController {

    // Контроллер работает только с сервисом
    private final UserService userService;

    // Создание пользователя
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    // Обновление пользователя
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    // Получение всех пользователей
    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    // Получение пользователя по id (GET /users/{id})
    @GetMapping("/{id}")
    public User getById(
            @PathVariable
            @Positive(message = "Id должен быть положительным") // ИЗМЕНЕНО
            int id
    ) {
        return userService.getById(id);
    }

    // Добавление в друзья (PUT /users/{id}/friends/{friendId})
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable("id")
            @Positive(message = "Id пользователя должен быть положительным") // ИЗМЕНЕНО
            int id,

            @PathVariable("friendId")
            @Positive(message = "Id друга должен быть положительным") // ИЗМЕНЕНО
            int friendId
    ) {
        userService.addFriend(id, friendId);
    }

    // Удаление из друзей (DELETE /users/{id}/friends/{friendId})
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(
            @PathVariable("id")
            @Positive(message = "Id пользователя должен быть положительным") // ИЗМЕНЕНО
            int id,

            @PathVariable("friendId")
            @Positive(message = "Id друга должен быть положительным") // ИЗМЕНЕНО
            int friendId
    ) {
        userService.removeFriend(id, friendId);
    }

    // Получение списка друзей пользователя (GET /users/{id}/friends)
    @GetMapping("/{id}/friends")
    public List<User> getFriends(
            @PathVariable("id")
            @Positive(message = "Id пользователя должен быть положительным") // ИЗМЕНЕНО
            int id
    ) {
        return userService.getFriends(id);
    }

    // Получение общих друзей (GET /users/{id}/friends/common/{otherId})
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(
            @PathVariable("id")
            @Positive(message = "Id пользователя должен быть положительным") // ИЗМЕНЕНО
            int id,

            @PathVariable("otherId")
            @Positive(message = "Id второго пользователя должен быть положительным") // ИЗМЕНЕНО
            int otherId
    ) {
        return userService.getCommonFriends(id, otherId);
    }
}