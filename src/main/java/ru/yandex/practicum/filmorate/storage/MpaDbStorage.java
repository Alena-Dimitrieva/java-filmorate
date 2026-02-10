package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    // SQL запросы

    private static final String SELECT_ALL_MPA =
            "SELECT id, name FROM mpa ORDER BY id";

    private static final String SELECT_MPA_BY_ID =
            "SELECT id, name FROM mpa WHERE id = ?";

    @Override
    public List<Mpa> getAll() {
        return jdbcTemplate.query(
                SELECT_ALL_MPA,
                (rs, rowNum) -> new Mpa(
                        rs.getInt("id"),
                        rs.getString("name")
                )
        );
    }

    @Override
    public Optional<Mpa> getById(int id) {
        List<Mpa> mpas = jdbcTemplate.query(
                SELECT_MPA_BY_ID,
                (rs, rowNum) -> new Mpa(
                        rs.getInt("id"),
                        rs.getString("name")
                ),
                id
        );
        return mpas.stream().findFirst();
    }
}