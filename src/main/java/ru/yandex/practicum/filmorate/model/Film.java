package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
public class Film {
    private int id;
    @NotBlank(message = "name is blank")
    private String name;
    @NotBlank(message = "description is blank")
    @Size(max = 200, message = "max size description 200 symbols")
    private String description;
    @Past(message = "release date cannot be in future")
    private LocalDate releaseDate;
    @NotNull
    @Positive(message = "duration is negative")
    private Integer duration;
}
