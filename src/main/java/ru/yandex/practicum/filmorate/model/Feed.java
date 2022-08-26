package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Feed {
    private long timestamp;
    private long userId;
    private EventType eventType;
    private Operation operation;
    private long eventId;
    private long entityId;
    
    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("TIMESTAMP", timestamp);
        values.put("USER_ID", userId);
        values.put("EVENT_TYPE", eventType.toString());
        values.put("OPERATION", operation.toString());
        values.put("EVENT_ID", eventId);
        values.put("ENTITY_ID", entityId);
        return values;
    }
}