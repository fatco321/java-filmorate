package ru.yandex.practicum.filmorate.service.serviseinterface;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedService {
    List<Feed> getFeedsByUserId(long userId);

}
