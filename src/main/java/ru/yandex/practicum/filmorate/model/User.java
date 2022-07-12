package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class User {
    private long id;
    @Email(message = "email not valid")
    private String email;
    @NotBlank(message = "login not valid")
    private String login;
    private String name;
    @Past(message = "birthday in future")
    @NonNull
    private LocalDate birthday;
    private Set<Long> friendsId;
}
