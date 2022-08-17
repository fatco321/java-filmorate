package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class Director {
    
    @NonNull
    private long id;
    
    @NotBlank(message = "name is blank")
    private String name;
    
    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("DIRECTOR_ID", id);
        values.put("DIRECTOR_NAME", name);
        return values;
    }
}
