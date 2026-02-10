package ru.yandex.practicum.filmorate.servicetests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIntegrationTest {

    private final JdbcTemplate jdbcTemplate;
    private UserService userService;

    @BeforeEach
    void setUp() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userService = new UserService(userStorage);

        // Очистка таблиц перед каждым тестом
        jdbcTemplate.update("DELETE FROM user_friends");
        jdbcTemplate.update("DELETE FROM users");
    }

    private User createSampleUser(String email, String login, String name) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return userService.create(user);
    }

    @Test
    void createAndGetUser_shouldWorkWithDatabase() {
        User user = createSampleUser("u1@test.com", "user1", "User1");

        User fetched = userService.getById(user.getId());

        assertThat(fetched.getId()).isPositive();
        assertThat(fetched.getEmail()).isEqualTo("u1@test.com");
        assertThat(fetched.getName()).isEqualTo("User1");
    }

    @Test
    void createUser_shouldApplyDefaultName_whenNameIsEmpty() {
        User user = createSampleUser("u2@test.com", "user2", "");

        assertThat(user.getName()).isEqualTo("user2"); // имя по умолчанию = login
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        createSampleUser("u1@test.com", "user1", "User1");
        createSampleUser("u2@test.com", "user2", "User2");

        List<User> users = userService.findAll();

        assertThat(users).hasSize(2)
                .extracting(User::getLogin)
                .containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    void updateUser_shouldModifyFields() {
        User user = createSampleUser("u1@test.com", "user1", "User1");

        user.setName("UpdatedName");
        user.setEmail("updated@test.com");

        User updated = userService.update(user);

        assertThat(updated.getName()).isEqualTo("UpdatedName");
        assertThat(updated.getEmail()).isEqualTo("updated@test.com");
    }

    @Test
    void addFriendAndGetFriends_shouldPersistInDb() {
        User u1 = createSampleUser("u1@test.com", "user1", "User1");
        User u2 = createSampleUser("u2@test.com", "user2", "User2");

        userService.addFriend(u1.getId(), u2.getId());

        List<User> friends = userService.getFriends(u1.getId());
        assertThat(friends).hasSize(1)
                .extracting(User::getId)
                .contains(u2.getId());
    }

    @Test
    void removeFriend_shouldUpdateFriendsList() {
        User u1 = createSampleUser("u1@test.com", "user1", "User1");
        User u2 = createSampleUser("u2@test.com", "user2", "User2");

        userService.addFriend(u1.getId(), u2.getId());
        userService.removeFriend(u1.getId(), u2.getId());

        List<User> friends = userService.getFriends(u1.getId());
        assertThat(friends).isEmpty();
    }

    @Test
    void getCommonFriends_shouldReturnSharedFriends() {
        User u1 = createSampleUser("u1@test.com", "user1", "User1");
        User u2 = createSampleUser("u2@test.com", "user2", "User2");
        User u3 = createSampleUser("u3@test.com", "user3", "User3");

        userService.addFriend(u1.getId(), u3.getId());
        userService.addFriend(u2.getId(), u3.getId());

        List<User> common = userService.getCommonFriends(u1.getId(), u2.getId());
        assertThat(common).hasSize(1)
                .first()
                .extracting(User::getId)
                .isEqualTo(u3.getId());
    }

    @Test
    void getById_shouldThrowException_whenUserNotFound() {
        assertThrows(RuntimeException.class, () -> userService.getById(999));
    }

    @Test
    void createUser_shouldThrowValidationException_onInvalidEmail() {
        User user = new User();
        user.setLogin("login");
        user.setEmail("invalid-email"); // без @
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(RuntimeException.class, () -> userService.create(user));
    }
}

