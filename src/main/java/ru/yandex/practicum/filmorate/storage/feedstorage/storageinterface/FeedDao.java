package ru.yandex.practicum.filmorate.storage.feedstorage.storageinterface;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.util.List;

public interface FeedDao {

    void createFeed(Long id, Long entityId, EventType eventType, Operation operation);

    List<Feed> getUserFeeds(long userId);

}
