package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.annotation.AfterFirstFilmCheck;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Film {
    private long id;
    @NotBlank(message = "name is blank")
    private String name;
    @NotBlank(message = "description is blank")
    @Size(max = 200, message = "max size description 200 symbols")
    private String description;
    @AfterFirstFilmCheck(message = "The release date cannot be earlier than December 28, 1895")
    private LocalDate releaseDate;
    @NotNull
    @Positive(message = "duration is negative")
    private Integer duration;
    private Set<Long> usersLike;
    private Set<Genre> genres;
    @NotNull
    private Mpa mpa;
    private int rate;
    private Set<Director> directors;
    private double markAvg;
    
    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("film_name", name);
        values.put("film_description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("mpa_id", mpa.getId());
        values.put("film_rate", rate);
        return values;
    }
}
