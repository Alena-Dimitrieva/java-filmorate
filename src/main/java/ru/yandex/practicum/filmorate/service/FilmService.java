package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    // Минимально допустимая дата релиза фильма
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    // Создание фильма после прохождения валидации
    public Film create(Film film) {
        validate(film);
        return filmStorage.create(film);
    }

    /**
     * Обновление фильма.
     * Проверяет корректность ID и существование фильма в хранилище.
     */
    public Film update(Film film) {
        if (film.getId() <= 0) {
            throw new ValidationException("Id должен быть положительным");
        }

        validate(film);

        // Наличие фильма с таким ID обязательно
        filmStorage.getById(film.getId())
                .orElseThrow(() -> new NoSuchElementException("Фильм с таким id не найден"));

        return filmStorage.update(film);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    // Дополнительная валидация, которую нельзя выразить стандартными аннотациями
    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не раньше 28 декабря 1895 года");
        }
    }
}