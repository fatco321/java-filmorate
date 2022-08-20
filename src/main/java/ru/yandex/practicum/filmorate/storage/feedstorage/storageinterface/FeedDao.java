package ru.yandex.practicum.filmorate.storage.feedstorage.storageinterface;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedDao {
    
    Feed createFeed(Feed feed);

    List<Feed> getUserFeeds(long userId);

}
