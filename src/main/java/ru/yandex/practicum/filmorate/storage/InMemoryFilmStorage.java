package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 1;

    //хранение лайков: filmId -> set of userId
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();

    @Override
    public Film create(Film film) {
        film.setId(idCounter++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        // Проверка существования фильма перед обновлением
        if (!films.containsKey(film.getId())) {
            throw new NoSuchElementException("Фильм с таким id не найден");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> getById(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    //поставить лайк фильму
    @Override
    public void addLike(int filmId, int userId) {
        likes.computeIfAbsent(filmId, id -> new HashSet<>()).add(userId);
    }

    //удалить лайк
    @Override
    public void removeLike(int filmId, int userId) {
        if (likes.containsKey(filmId)) {
            likes.get(filmId).remove(userId);
        }
    }

    //получить список популярных фильмов по количеству лайков
    @Override
    public List<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted((f1, f2) -> Integer.compare(
                        likes.getOrDefault(f2.getId(), Set.of()).size(),
                        likes.getOrDefault(f1.getId(), Set.of()).size()
                ))
                .limit(count)
                .collect(Collectors.toList());
    }
}