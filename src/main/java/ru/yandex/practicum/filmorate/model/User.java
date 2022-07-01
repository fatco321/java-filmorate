package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private Integer id;
    @Email(message = "email not valid")
    private String email;
    @NotBlank(message = "login not valid")
    private String login;
    private String name;
    @Past(message = "birthday in future")
    @NonNull
    private LocalDate birthday;
}
