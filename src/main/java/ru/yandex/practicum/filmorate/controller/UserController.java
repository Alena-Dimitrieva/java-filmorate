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
}