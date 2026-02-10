package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    // SQL запросы

    private static final String INSERT_FILM = """
        INSERT INTO films (name, description, release_date, duration, mpa_rating_id)
        VALUES (?, ?, ?, ?, ?)
        """;

    private static final String UPDATE_FILM = """
        UPDATE films
        SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ?
        WHERE id = ?
        """;

    private static final String SELECT_FILM_BY_ID = """
        SELECT id, name, description, release_date, duration, mpa_rating_id
        FROM films
        WHERE id = ?
        """;

    private static final String SELECT_ALL_FILMS = """
        SELECT id, name, description, release_date, duration, mpa_rating_id
        FROM films
        """;

    private static final String SELECT_POPULAR_FILMS = """
        SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id
        FROM films f
        LEFT JOIN film_likes fl ON f.id = fl.film_id
        GROUP BY f.id
        ORDER BY COUNT(fl.user_id) DESC
        LIMIT ?
        """;

    private static final String INSERT_LIKE =
            "MERGE INTO film_likes (film_id, user_id) KEY (film_id, user_id) VALUES (?, ?)";

    private static final String DELETE_LIKE =
            "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";

    private static final String DELETE_FILM_GENRES =
            "DELETE FROM film_genres WHERE film_id = ?";

    private static final String INSERT_FILM_GENRE =
            "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

    private static final String SELECT_MPA_BY_ID =
            "SELECT id, name FROM mpa WHERE id = ?";

    private static final String SELECT_GENRES_BY_FILM_ID = """
        SELECT g.id, g.name
        FROM genres g
        JOIN film_genres fg ON g.id = fg.genre_id
        WHERE fg.film_id = ?
        """;

    private static final String SELECT_LIKES_BY_FILM_ID =
            "SELECT user_id FROM film_likes WHERE film_id = ?";

    // CRUD

    @Override
    public Film create(Film film) {
        if (film.getMpa() == null) {
            throw new ValidationException("MPA рейтинг обязателен");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_FILM, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        updateFilmGenres(film.getId(), film.getGenres());

        return getById(film.getId())
                .orElseThrow(() -> new NotFoundException("Фильм после создания не найден"));
    }

    @Override
    public Film update(Film film) {
        if (getById(film.getId()).isEmpty()) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }

        jdbcTemplate.update(
                UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        updateFilmGenres(film.getId(), film.getGenres());

        return getById(film.getId())
                .orElseThrow(() -> new NotFoundException("Фильм после обновления не найден"));
    }

    @Override
    public Optional<Film> getById(int id) {
        List<Film> films = jdbcTemplate.query(
                SELECT_FILM_BY_ID,
                (rs, rn) -> mapRowToFilm(rs),
                id
        );
        return films.stream().findFirst();
    }

    @Override
    public List<Film> findAll() {
        return jdbcTemplate.query(
                SELECT_ALL_FILMS,
                (rs, rn) -> mapRowToFilm(rs)
        );
    }

    @Override
    public void addLike(int filmId, int userId) {
        jdbcTemplate.update(INSERT_LIKE, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        jdbcTemplate.update(DELETE_LIKE, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return jdbcTemplate.query(
                SELECT_POPULAR_FILMS,
                (rs, rn) -> mapRowToFilm(rs),
                count
        );
    }

    private void updateFilmGenres(int filmId, Set<Genre> genres) {
        jdbcTemplate.update(DELETE_FILM_GENRES, filmId);

        if (genres == null || genres.isEmpty()) return;

        for (Genre genre : genres) {
            jdbcTemplate.update(INSERT_FILM_GENRE, filmId, genre.getId());
        }
    }

    private Film mapRowToFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));

        if (rs.getDate("release_date") != null) {
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        }

        film.setDuration(rs.getInt("duration"));

        Integer mpaId = rs.getInt("mpa_rating_id");
        Mpa mpa = jdbcTemplate.queryForObject(
                SELECT_MPA_BY_ID,
                (mRs, rn) -> new Mpa(mRs.getInt("id"), mRs.getString("name")),
                mpaId
        );
        film.setMpa(mpa);

        List<Genre> genreList = jdbcTemplate.query(
                SELECT_GENRES_BY_FILM_ID,
                (gRs, rn) -> new Genre(gRs.getInt("id"), gRs.getString("name")),
                film.getId()
        );
        film.setGenres(new LinkedHashSet<>(genreList));

        List<Integer> likes = jdbcTemplate.query(
                SELECT_LIKES_BY_FILM_ID,
                (lRs, rn) -> lRs.getInt("user_id"),
                film.getId()
        );
        film.setLikes(new HashSet<>(likes));

        return film;
    }
}
