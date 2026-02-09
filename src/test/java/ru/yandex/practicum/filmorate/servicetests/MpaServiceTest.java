package ru.yandex.practicum.filmorate.servicetests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class MpaServiceTest {

    private MpaStorage mpaStorage;
    private MpaService mpaService;

    @BeforeEach
    void setUp() {
        mpaStorage = Mockito.mock(MpaStorage.class);
        mpaService = new MpaService(mpaStorage);
    }

    @Test
    void findAll_shouldReturnAllMpa() {
        when(mpaStorage.getAll()).thenReturn(List.of(
                new Mpa(1, "G"),
                new Mpa(2, "PG")
        ));

        List<Mpa> mpas = mpaService.findAll();

        assertThat(mpas).hasSize(2)
                .extracting(Mpa::getName)
                .containsExactly("G", "PG");

        verify(mpaStorage, times(1)).getAll();
    }

    @Test
    void getById_shouldReturnMpa_whenExists() {
        when(mpaStorage.getById(1)).thenReturn(Optional.of(new Mpa(1, "G")));

        Mpa mpa = mpaService.getById(1);

        assertThat(mpa.getName()).isEqualTo("G");
        verify(mpaStorage, times(1)).getById(1);
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(mpaStorage.getById(99)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> mpaService.getById(99));

        verify(mpaStorage, times(1)).getById(99);
    }
}


