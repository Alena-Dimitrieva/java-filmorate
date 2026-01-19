package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Enum.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1; //Друзья теперь хранятся внутри объекта User (user.getFriends()).

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
        User user = users.get(userId);
        User friend = users.get(friendId);

        if (user != null && friend != null) {
            user.getFriends().put(friendId, FriendshipStatus.UNCONFIRMED);
            friend.getFriends().put(userId, FriendshipStatus.UNCONFIRMED);
        }
    }

    @Override
    public void confirmFriend(int userId, int friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);

        if (user != null && friend != null) {
            user.getFriends().put(friendId, FriendshipStatus.CONFIRMED);
            friend.getFriends().put(userId, FriendshipStatus.CONFIRMED);
        }
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);

        if (user != null) {
            user.getFriends().remove(friendId);
        }
        if (friend != null) {
            friend.getFriends().remove(userId);
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

        return first.getFriends().keySet().stream()
                .filter(second.getFriends().keySet()::contains)
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}