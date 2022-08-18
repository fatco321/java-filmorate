package ru.yandex.practicum.filmorate.storage.feedstorage.storageinterface;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedDao {
    
    Feed createFeed(Feed feed);
    
    void updateFeed(Feed feed);
    
    Feed getFeedById(long feedId);
    
    void deleteFeedById(long feedId);
    
    List<Feed> getUserFeeds(long userId);
    
    void deleteAllFeeds();
    
    boolean isFeedExist(long feedId);
}
