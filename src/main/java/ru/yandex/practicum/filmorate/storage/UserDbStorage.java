package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Enum.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * DAO для работы с пользователями через базу данных H2.
 * Полностью соответствует ТЗ:
 * таблицы users и user_friends
 * хранение друзей и их статусов внутри объекта User
 * односторонняя дружба (заявка -> CONFIRMED после подтверждения)
 */
@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    // Создание нового пользователя
    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        // Получаем сгенерированный id
        Integer id = jdbcTemplate.queryForObject("SELECT MAX(id) FROM users", Integer.class);
        user.setId(Objects.requireNonNull(id));

        return user;
    }

    // Обновление существующего пользователя
    @Override
    public User update(User user) {
        getById(user.getId()); // проверка, что пользователь существует

        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

        return user;
    }

    // Получение пользователя по id
    @Override
    public Optional<User> getById(int id) {
        String sql = "SELECT id, email, login, name, birthday FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToUser(rs), id);
        return users.stream().findFirst();
    }

    // Получение всех пользователей
    @Override
    public List<User> findAll() {
        String sql = "SELECT id, email, login, name, birthday FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToUser(rs));
    }

    // Добавление друга (односторонняя заявка)
    @Override
    public void addFriend(int userId, int friendId) {
        String sql = "MERGE INTO user_friends (user_id, friend_id, status) KEY (user_id, friend_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, FriendshipStatus.REQUESTED.name());
    }

    // Подтверждение заявки в друзья
    @Override
    public void confirmFriend(int userId, int friendId) {
        String sql = "UPDATE user_friends SET status = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, FriendshipStatus.CONFIRMED.name(), userId, friendId);
    }

    // Удаление друга
    @Override
    public void removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    // Получение списка друзей пользователя
    @Override
    public List<User> getFriends(int userId) {
        String sql = "SELECT u.id, u.email, u.login, u.name, u.birthday, uf.status " +
                "FROM users u " +
                "JOIN user_friends uf ON u.id = uf.friend_id " +
                "WHERE uf.user_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            User user = mapRowToUser(rs);
            FriendshipStatus status = FriendshipStatus.valueOf(rs.getString("status"));
            user.getFriends().put(rs.getInt("id"), status); // ключ — id друга
            return user;
        }, userId);
    }

    // Получение общих друзей двух пользователей
    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        String sql = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM users u " +
                "JOIN user_friends uf1 ON u.id = uf1.friend_id " +
                "JOIN user_friends uf2 ON u.id = uf2.friend_id " +
                "WHERE uf1.user_id = ? AND uf2.user_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToUser(rs), userId, otherId);
    }

    /**
     * Маппинг строки ResultSet в объект User.
     * Загружает друзей пользователя и их статусы.
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        if (rs.getDate("birthday") != null) {
            user.setBirthday(rs.getDate("birthday").toLocalDate());
        }

        // Загрузка друзей для этого пользователя
        String sqlFriends = "SELECT friend_id, status FROM user_friends WHERE user_id = ?";
        List<Map<String, Object>> friendList = jdbcTemplate.queryForList(sqlFriends, user.getId());
        for (Map<String, Object> f : friendList) {
            int friendId = (Integer) f.get("friend_id");
            FriendshipStatus status = FriendshipStatus.valueOf((String) f.get("status"));
            user.getFriends().put(friendId, status);
        }

        return user;
    }
}