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

    // SQL запросы

    private static final String INSERT_USER =
            "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";

    private static final String SELECT_LAST_USER_ID =
            "SELECT MAX(id) FROM users";

    private static final String UPDATE_USER =
            "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";

    private static final String SELECT_USER_BY_ID =
            "SELECT id, email, login, name, birthday FROM users WHERE id = ?";

    private static final String SELECT_ALL_USERS =
            "SELECT id, email, login, name, birthday FROM users";

    private static final String ADD_FRIEND =
            "MERGE INTO user_friends (user_id, friend_id, status) KEY (user_id, friend_id) VALUES (?, ?, ?)";

    private static final String CONFIRM_FRIEND =
            "UPDATE user_friends SET status = ? WHERE user_id = ? AND friend_id = ?";

    private static final String REMOVE_FRIEND =
            "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";

    private static final String SELECT_FRIENDS =
            "SELECT u.id, u.email, u.login, u.name, u.birthday, uf.status " +
                    "FROM users u " +
                    "JOIN user_friends uf ON u.id = uf.friend_id " +
                    "WHERE uf.user_id = ?";

    private static final String SELECT_COMMON_FRIENDS =
            "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                    "FROM users u " +
                    "JOIN user_friends uf1 ON u.id = uf1.friend_id " +
                    "JOIN user_friends uf2 ON u.id = uf2.friend_id " +
                    "WHERE uf1.user_id = ? AND uf2.user_id = ?";

    private static final String SELECT_USER_FRIENDS_WITH_STATUS =
            "SELECT friend_id, status FROM user_friends WHERE user_id = ?";

    // СRUD

    @Override
    public User create(User user) {
        jdbcTemplate.update(
                INSERT_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );

        Integer id = jdbcTemplate.queryForObject(SELECT_LAST_USER_ID, Integer.class);
        user.setId(Objects.requireNonNull(id));

        return user;
    }

    @Override
    public User update(User user) {
        getById(user.getId());

        jdbcTemplate.update(
                UPDATE_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );

        return user;
    }

    @Override
    public Optional<User> getById(int id) {
        List<User> users = jdbcTemplate.query(
                SELECT_USER_BY_ID,
                (rs, rowNum) -> mapRowToUser(rs),
                id
        );
        return users.stream().findFirst();
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query(
                SELECT_ALL_USERS,
                (rs, rowNum) -> mapRowToUser(rs)
        );
    }

    // ===== Friends =====

    @Override
    public void addFriend(int userId, int friendId) {
        jdbcTemplate.update(
                ADD_FRIEND,
                userId,
                friendId,
                FriendshipStatus.REQUESTED.name()
        );
    }

    @Override
    public void confirmFriend(int userId, int friendId) {
        jdbcTemplate.update(
                CONFIRM_FRIEND,
                FriendshipStatus.CONFIRMED.name(),
                userId,
                friendId
        );
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        jdbcTemplate.update(
                REMOVE_FRIEND,
                userId,
                friendId
        );
    }

    @Override
    public List<User> getFriends(int userId) {
        return jdbcTemplate.query(
                SELECT_FRIENDS,
                (rs, rowNum) -> {
                    User user = mapRowToUser(rs);
                    FriendshipStatus status = FriendshipStatus.valueOf(rs.getString("status"));
                    user.getFriends().put(rs.getInt("id"), status);
                    return user;
                },
                userId
        );
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        return jdbcTemplate.query(
                SELECT_COMMON_FRIENDS,
                (rs, rowNum) -> mapRowToUser(rs),
                userId,
                otherId
        );
    }

    /**
     * Маппинг ResultSet → User + загрузка друзей
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

        List<Map<String, Object>> friendList =
                jdbcTemplate.queryForList(SELECT_USER_FRIENDS_WITH_STATUS, user.getId());

        for (Map<String, Object> f : friendList) {
            int friendId = (Integer) f.get("friend_id");
            FriendshipStatus status = FriendshipStatus.valueOf((String) f.get("status"));
            user.getFriends().put(friendId, status);
        }

        return user;
    }
}