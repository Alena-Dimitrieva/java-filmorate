package ru.yandex.practicum.filmorate.mapper;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


class UserMapperTest {

    @Test
    void toDto_shouldMapAllFieldsFromUser() {
        User user = new User();
        user.setId(1);
        user.setEmail("user@mail.ru");
        user.setLogin("login");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(1995, 5, 20));

        UserDto dto = UserMapper.toDto(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getEmail()).isEqualTo("user@mail.ru");
        assertThat(dto.getLogin()).isEqualTo("login");
        assertThat(dto.getName()).isEqualTo("Имя");
        assertThat(dto.getBirthday()).isEqualTo(LocalDate.of(1995, 5, 20));
    }

    @Test
    void fromDto_shouldMapAllFieldsFromDto() {
        UserDto dto = new UserDto();
        dto.setId(2);
        dto.setEmail("dto@mail.ru");
        dto.setLogin("dtoLogin");
        dto.setName("DTO имя");
        dto.setBirthday(LocalDate.of(2000, 1, 1));

        User user = UserMapper.fromDto(dto);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(2);
        assertThat(user.getEmail()).isEqualTo("dto@mail.ru");
        assertThat(user.getLogin()).isEqualTo("dtoLogin");
        assertThat(user.getName()).isEqualTo("DTO имя");
        assertThat(user.getBirthday()).isEqualTo(LocalDate.of(2000, 1, 1));
    }

    @Test
    void fromDto_shouldApplyDtoNormalizationLogic() {
        UserDto dto = new UserDto();
        dto.setId(3);
        dto.setEmail("   ");   // станет null
        dto.setLogin("login");
        dto.setName("");       // станет null
        dto.setBirthday(LocalDate.of(1990, 1, 1));

        User user = UserMapper.fromDto(dto);

        assertThat(user.getEmail()).isNull();
        assertThat(user.getName()).isNull();
        assertThat(user.getLogin()).isEqualTo("login");
    }
}
