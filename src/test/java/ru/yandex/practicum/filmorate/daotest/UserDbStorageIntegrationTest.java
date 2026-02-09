package ru.yandex.practicum.filmorate.daotest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.Enum.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageIntegrationTest {

    private final JdbcTemplate jdbcTemplate;

    private UserDbStorage userDbStorage;

    @BeforeEach
    void setUp() {
        userDbStorage = new UserDbStorage(jdbcTemplate);

        // Очищаем таблицы
        jdbcTemplate.update("DELETE FROM user_friends");
        jdbcTemplate.update("DELETE FROM users");

        // Инициализация пользователей
        jdbcTemplate.update("""
                    INSERT INTO users (id, email, login, name, birthday) VALUES
                    (1, 'user1@example.com', 'user1', 'Алена', '1990-05-12'),
                    (2, 'user2@example.com', 'user2', 'Иван', '1985-03-23'),
                    (3, 'user3@example.com', 'user3', 'Мария', '2000-11-01')
                """);

        // Инициализация дружбы
        jdbcTemplate.update("""
                    INSERT INTO user_friends (user_id, friend_id, status) VALUES
                    (1, 2, 'CONFIRMED'),
                    (2, 3, 'REQUESTED')
                """);
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        List<User> users = userDbStorage.findAll();
        assertThat(users).hasSize(3);
    }

    @Test
    void getById_shouldReturnUser() {
        Optional<User> userOptional = userDbStorage.getById(1);
        assertThat(userOptional).isPresent();
        User user = userOptional.get();
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getFriends()).containsKey(2);
        assertThat(user.getFriends().get(2)).isEqualTo(FriendshipStatus.CONFIRMED);
    }

    @Test
    void getById_shouldReturnEmpty_whenUserNotFound() {
        Optional<User> userOptional = userDbStorage.getById(999);
        assertThat(userOptional).isEmpty();
    }

    @Test
    void create_shouldAddUser() {
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setLogin("newuser");
        newUser.setName("Новый пользователь");
        newUser.setBirthday(LocalDate.of(1995, 6, 15));

        User created = userDbStorage.create(newUser);
        assertThat(created.getId()).isPositive();

        Optional<User> fetched = userDbStorage.getById(created.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void update_shouldModifyUser() {
        User user = userDbStorage.getById(1).orElseThrow();
        user.setName("Изменённое имя");
        user.setEmail("updated@example.com");

        User updated = userDbStorage.update(user);
        assertThat(updated.getName()).isEqualTo("Изменённое имя");
        assertThat(updated.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void addFriend_shouldCreateRequestedFriendship() {
        userDbStorage.addFriend(1, 3);

        User user = userDbStorage.getById(1).orElseThrow();
        assertThat(user.getFriends()).containsEntry(3, FriendshipStatus.REQUESTED);
    }

    @Test
    void confirmFriend_shouldUpdateFriendshipStatus() {
        userDbStorage.confirmFriend(2, 3);

        User user = userDbStorage.getById(2).orElseThrow();
        assertThat(user.getFriends()).containsEntry(3, FriendshipStatus.CONFIRMED);
    }

    @Test
    void removeFriend_shouldDeleteFriendship() {
        userDbStorage.removeFriend(1, 2);

        User user = userDbStorage.getById(1).orElseThrow();
        assertThat(user.getFriends()).doesNotContainKey(2);
    }

    @Test
    void getFriends_shouldReturnAllFriends() {
        List<User> friends = userDbStorage.getFriends(1);
        assertThat(friends).hasSize(1);
        assertThat(friends.getFirst().getId()).isEqualTo(2);
    }

    @Test
    void getCommonFriends_shouldReturnSharedFriends() {
        // Добавим общую дружбу
        userDbStorage.addFriend(3, 2); // Мария -> Иван
        List<User> common = userDbStorage.getCommonFriends(1, 3);
        assertThat(common).hasSize(1);
        assertThat(common.getFirst().getId()).isEqualTo(2);
    }
}

