package ru.yandex.practicum.filmorate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public void setName(String name) {
        this.name = (name == null || name.isBlank()) ? null : name;
    }

    public void setEmail(String email) {
        this.email = (email == null || email.isBlank()) ? null : email;
    }

    public void setLogin(String login) {
        this.login = (login == null || login.isBlank()) ? null : login;
    }
}