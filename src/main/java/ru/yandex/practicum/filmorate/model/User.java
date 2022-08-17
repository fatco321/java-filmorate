package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
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
    
    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("user_name", name);
        values.put("email", email);
        values.put("login", login);
        values.put("birthday", birthday);
        return values;
    }
}
