package ru.yandex.practicum.filmorate.memorytests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Enum.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class InMemoryUserStorageTest {

    private InMemoryUserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
    }

    @Test
    void createAndGetById_shouldWork() {
        User u = new User();
        u.setEmail("a@test.com");
        u.setLogin("user1");
        u.setBirthday(LocalDate.of(1990, 1, 1));

        User saved = userStorage.create(u);
        assertThat(saved.getId()).isPositive();

        User fetched = userStorage.getById(saved.getId()).orElseThrow();
        assertThat(fetched.getLogin()).isEqualTo("user1");
    }

    @Test
    void update_shouldReplaceUser() {
        User u = new User();
        u.setEmail("b@test.com");
        u.setLogin("user2");

        u = userStorage.create(u);
        u.setLogin("updated");
        User updated = userStorage.update(u);

        assertThat(updated.getLogin()).isEqualTo("updated");
    }

    @Test
    void addConfirmRemoveFriend_shouldWork() {
        User u1 = new User();
        u1.setLogin("A");
        u1 = userStorage.create(u1);
        User u2 = new User();
        u2.setLogin("B");
        u2 = userStorage.create(u2);

        userStorage.addFriend(u1.getId(), u2.getId());
        assertThat(u1.getFriends().get(u2.getId())).isEqualTo(FriendshipStatus.REQUESTED);

        userStorage.confirmFriend(u1.getId(), u2.getId());
        assertThat(u1.getFriends().get(u2.getId())).isEqualTo(FriendshipStatus.CONFIRMED);

        userStorage.removeFriend(u1.getId(), u2.getId());
        assertThat(u1.getFriends()).doesNotContainKey(u2.getId());
    }

    @Test
    void getCommonFriends_shouldReturnCorrectly() {
        User u1 = new User();
        u1.setLogin("A");
        u1 = userStorage.create(u1);
        User u2 = new User();
        u2.setLogin("B");
        u2 = userStorage.create(u2);
        User u3 = new User();
        u3.setLogin("C");
        u3 = userStorage.create(u3);

        userStorage.addFriend(u1.getId(), u2.getId());
        userStorage.addFriend(u3.getId(), u2.getId());

        List<User> common = userStorage.getCommonFriends(u1.getId(), u3.getId());
        assertThat(common).hasSize(1).first().extracting(User::getLogin).isEqualTo("B");
    }

    @Test
    void update_nonexistent_shouldThrow() {
        User u = new User();
        u.setId(999);
        assertThrows(NoSuchElementException.class, () -> userStorage.update(u));
    }
}

