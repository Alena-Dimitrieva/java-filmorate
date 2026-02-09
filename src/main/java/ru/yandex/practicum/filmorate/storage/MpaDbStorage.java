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

    @Override
    public List<Mpa> getAll() {
        String sql = "SELECT id, name FROM mpa ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Mpa(
                        rs.getInt("id"),
                        rs.getString("name")
                )
        );
    }

    @Override
    public Optional<Mpa> getById(int id) {
        String sql = "SELECT id, name FROM mpa WHERE id = ?";
        List<Mpa> mpas = jdbcTemplate.query(sql,
                (rs, rowNum) -> new Mpa(rs.getInt("id"), rs.getString("name")),
                id);
        return mpas.stream().findFirst();
    }

}