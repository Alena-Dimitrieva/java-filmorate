package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public List<Mpa> findAll() {
        return mpaStorage.getAll();
    }

    public Mpa getById(int id) {
        // Если MPA не найден - бросаем NoSuchElementException
        return mpaStorage.getById(id)
                .orElseThrow(() -> new NoSuchElementException("MPA с id=" + id + " не найден"));
    }
}
