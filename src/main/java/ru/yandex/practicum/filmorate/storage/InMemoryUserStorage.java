package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    // Хранение друзей: userId -> множество friendId
    private final Map<Integer, Set<Integer>> friends = new HashMap<>();

    @Override
    public User create(User user) {
        // Присвоение уникального id
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        // Проверка, существует ли пользователь перед обновлением
        if (!users.containsKey(user.getId())) {
            throw new NoSuchElementException("Пользователь с таким id не найден");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void addFriend(int userId, int friendId) {
        // Добавление взаимной дружбы
        friends.computeIfAbsent(userId, id -> new HashSet<>()).add(friendId);
        friends.computeIfAbsent(friendId, id -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        if (friends.containsKey(userId)) {
            friends.get(userId).remove(friendId);
        }
        if (friends.containsKey(friendId)) {
            friends.get(friendId).remove(userId);
        }
    }

    @Override
    public List<User> getFriends(int userId) {
        return friends.getOrDefault(userId, Set.of()).stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        Set<Integer> first = friends.getOrDefault(userId, Set.of());
        Set<Integer> second = friends.getOrDefault(otherId, Set.of());

        return first.stream()
                .filter(second::contains)
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
