package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Enum.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory реализация UserStorage.
 * Дружба теперь односторонняя:
 * пользователь добавляет другого в свой список друзей, но сам в его список не попадает.
 */
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    @Override
    public User create(User user) {
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
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

    /**
     * Добавление друга односторонне:
     * только в списке пользователя появляется друг
     */
    @Override
    public void addFriend(int userId, int friendId) {
        User user = users.get(userId);
        if (user != null && users.containsKey(friendId)) {
            user.getFriends().put(friendId, FriendshipStatus.REQUESTED);
        }
    }

    /**
     * Подтверждение дружбы:
     * меняется только статус у текущего пользователя, другого не трогаем
     */
    @Override
    public void confirmFriend(int userId, int friendId) {
        User user = users.get(userId);
        if (user != null && user.getFriends().containsKey(friendId)) {
            user.getFriends().put(friendId, FriendshipStatus.CONFIRMED);
        }
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        User user = users.get(userId);
        if (user != null) {
            user.getFriends().remove(friendId);
        }
    }

    @Override
    public List<User> getFriends(int userId) {
        User user = users.get(userId);
        if (user == null) {
            return List.of();
        }
        return user.getFriends().keySet().stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        User first = users.get(userId);
        User second = users.get(otherId);

        if (first == null || second == null) {
            return List.of();
        }

        // Считаем только друзей, которых оба пользователя добавили
        return first.getFriends().keySet().stream()
                .filter(second.getFriends().keySet()::contains)
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}