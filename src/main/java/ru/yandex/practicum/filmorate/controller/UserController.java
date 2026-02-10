package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    // Создание пользователя
    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        User user = UserMapper.fromDto(userDto);
        User saved = userService.create(user);
        return UserMapper.toDto(saved);
    }

    // Обновление пользователя
    @PutMapping
    public UserDto update(@Valid @RequestBody UserDto userDto) {
        User user = UserMapper.fromDto(userDto);
        User updated = userService.update(user);
        return UserMapper.toDto(updated);
    }

    // Получение всех пользователей
    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    // Получение пользователя по id
    @GetMapping("/{id}")
    public UserDto getById(
            @PathVariable
            @Positive(message = "Id должен быть положительным")
            int id
    ) {
        User user = userService.getById(id);
        return UserMapper.toDto(user);
    }

    // Добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable("id") @Positive(message = "Id пользователя должен быть положительным") int id,
            @PathVariable("friendId") @Positive(message = "Id друга должен быть положительным") int friendId
    ) {
        userService.addFriend(id, friendId);
    }

    // Удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(
            @PathVariable("id") @Positive(message = "Id пользователя должен быть положительным") int id,
            @PathVariable("friendId") @Positive(message = "Id друга должен быть положительным") int friendId
    ) {
        userService.removeFriend(id, friendId);
    }

    // Получение списка друзей пользователя
    @GetMapping("/{id}/friends")
    public List<UserDto> getFriends(
            @PathVariable("id") @Positive(message = "Id пользователя должен быть положительным") int id
    ) {
        return userService.getFriends(id).stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    // Получение общих друзей
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(
            @PathVariable("id") @Positive(message = "Id пользователя должен быть положительным") int id,
            @PathVariable("otherId") @Positive(message = "Id второго пользователя должен быть положительным") int otherId
    ) {
        return userService.getCommonFriends(id, otherId).stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }
}
