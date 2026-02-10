package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    // SQL запросы

    private static final String SELECT_ALL_GENRES =
            "SELECT id, name FROM genres ORDER BY id";

    private static final String SELECT_GENRE_BY_ID =
            "SELECT id, name FROM genres WHERE id = ?";


    private final RowMapper<Genre> genreRowMapper = (rs, rowNum) ->
            new Genre(
                    rs.getInt("id"),
                    rs.getString("name")
            );

    @Override
    public List<Genre> findAll() {
        return jdbcTemplate.query(SELECT_ALL_GENRES, genreRowMapper);
    }

    @Override
    public Optional<Genre> getById(int id) {
        List<Genre> result =
                jdbcTemplate.query(SELECT_GENRE_BY_ID, genreRowMapper, id);
        return result.stream().findFirst();
    }
}
