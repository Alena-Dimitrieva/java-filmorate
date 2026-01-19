package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Enum.MpaRating;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    // Минимально допустимая дата релиза фильма
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    // Создание фильма после прохождения валидации
    public Film create(Film film) {
        validate(film);
        initializeDefaults(film); //инициализация genres и mpaRating
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        // Проверка id убрана

        validate(film);
        initializeDefaults(film);

        filmStorage.getById(film.getId())
                .orElseThrow(() -> new NoSuchElementException("Фильм с таким id не найден"));

        return filmStorage.update(film);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getById(int id) {
        // Проверка id убрана

        return filmStorage.getById(id)
                .orElseThrow(() -> new NoSuchElementException("Фильм с таким id не найден"));
    }

    // поставить лайк фильму
    public void addLike(int filmId, int userId) {
        filmStorage.getById(filmId)
                .orElseThrow(() -> new NoSuchElementException("Фильм с таким id не найден"));

        userStorage.getById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с таким id не найден"));

        filmStorage.addLike(filmId, userId);
    }

    // удалить лайк
    public void removeLike(int filmId, int userId) {
        filmStorage.getById(filmId)
                .orElseThrow(() -> new NoSuchElementException("Фильм с таким id не найден"));

        userStorage.getById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с таким id не найден"));

        filmStorage.removeLike(filmId, userId);
    }

    // список популярных фильмов
    public List<Film> getPopularFilms(int count) {
        // Проверка count убрана

        return filmStorage.getPopularFilms(count);
    }

    private void initializeDefaults(Film film) {
        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }
        if (film.getMpaRating() == null) {
            film.setMpaRating(MpaRating.G); // по умолчанию без возрастных ограничений
        }
    }

    // Расширенная валидация по ТЗ
    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание не должно превышать 200 символов");
        }

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не раньше 28 декабря 1895 года");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительным числом");
        }
    }
}