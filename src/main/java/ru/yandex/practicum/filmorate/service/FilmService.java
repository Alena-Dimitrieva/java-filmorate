package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Enum.MpaRating;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreService genreService;

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            GenreService genreService
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreService = genreService;
    }

    public Film create(Film film) {
        validateBusinessRules(film);
        initializeDefaults(film);
        validateMpaAndGenres(film);

        Film created = filmStorage.create(film);
        log.info("Создан фильм: {} (id={})", created.getName(), created.getId());
        return created;
    }

    public Film update(Film film) {
        validateBusinessRules(film);
        initializeDefaults(film);
        validateMpaAndGenres(film);

        filmStorage.getById(film.getId())
                .orElseThrow(() -> new NoSuchElementException("Фильм с таким id не найден"));

        Film updated = filmStorage.update(film);
        log.info("Обновлен фильм: {} (id={})", updated.getName(), updated.getId());
        return updated;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getById(int id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NoSuchElementException("Фильм с таким id не найден"));
    }

    public void addLike(int filmId, int userId) {
        getById(filmId);
        userStorage.getById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с таким id не найден"));

        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        getById(filmId);
        userStorage.getById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с таким id не найден"));

        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        int limit = count > 0 ? count : 10;
        return filmStorage.getPopularFilms(limit);
    }

    private void initializeDefaults(Film film) {
        if (film.getGenres() == null) {
            film.setGenres(new LinkedHashSet<>());
        }
    }

    private void validateMpaAndGenres(Film film) {
        //MPA
        if (film.getMpa() == null) {
            throw new ValidationException("MPA рейтинг обязателен");
        }
        try {
            MpaRating rating = MpaRating.fromId(film.getMpa().getId());
            film.setMpa(new Mpa(rating.getId(), rating.getNameWithDash()));
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Неверный id MPA рейтинга: " + film.getMpa().getId());
        }

        //GENRES
        Set<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            Set<Genre> realGenres = new LinkedHashSet<>();

            for (Genre g : genres) {
                Genre real = genreService.getById(g.getId());
                if (real == null) {
                    throw new ValidationException("Неверный id жанра: " + g.getId());
                }
                // добавление в LinkedHashSet сохраняет исходный порядок
                realGenres.add(real);
            }

            film.setGenres(realGenres);
        }
    }

    private void validateBusinessRules(Film film) {
        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза фильма обязательна");
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительным числом");
        }
    }
}