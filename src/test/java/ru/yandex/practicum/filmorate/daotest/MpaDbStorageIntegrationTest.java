package ru.yandex.practicum.filmorate.daotest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageIntegrationTest {

    private final JdbcTemplate jdbcTemplate;

    private MpaDbStorage mpaDbStorage;

    @BeforeEach
    void setUp() {
        mpaDbStorage = new MpaDbStorage(jdbcTemplate);

        jdbcTemplate.update("""
                    MERGE INTO mpa (id, name) KEY(id) VALUES
                    (1, 'G'),
                    (2, 'PG'),
                    (3, 'PG-13')
                """);
    }

    @Test
    void getAll_shouldReturnAllMpaRatings() {
        List<Mpa> allMpas = mpaDbStorage.getAll();
        assertEquals(5, allMpas.size(), "Должно быть 5 рейтингов MPA");
    }

    @Test
    void getById_shouldReturnMpa_whenExists() {
        Mpa mpa = mpaDbStorage.getById(2).orElseThrow();
        assertEquals(2, mpa.getId());
        assertEquals("PG", mpa.getName());
    }

    @Test
    void getById_shouldReturnEmpty_whenNotFound() {
        Optional<Mpa> mpa = mpaDbStorage.getById(99);
        assertTrue(mpa.isEmpty());
    }
}
