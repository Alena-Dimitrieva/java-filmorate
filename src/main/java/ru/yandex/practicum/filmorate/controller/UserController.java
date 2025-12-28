package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    // Контроллер общается только с сервисом
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
    public User getById(@PathVariable int id) {
        return userService.getById(id);
    }

    // Добавление в друзья (PUT /users/{id}/friends/{friendId})
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        userService.addFriend(id, friendId);
    }

    // Удаление из друзей (DELETE /users/{id}/friends/{friendId})
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        userService.removeFriend(id, friendId);
    }

    // Получение списка друзей пользователя (GET /users/{id}/friends)
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable("id") int id) {
        return userService.getFriends(id);
    }

    // Получение общих друзей (GET /users/{id}/friends/common/{otherId})
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") int id, @PathVariable("otherId") int otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}