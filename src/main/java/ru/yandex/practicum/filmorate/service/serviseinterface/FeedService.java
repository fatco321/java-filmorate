package ru.yandex.practicum.filmorate.service.serviseinterface;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedService {
    
    Feed createFeed(Feed feed);
    
    void updateFeed(long feedId);
    
    Feed getFeedById(long feedId);
    
    List<Feed> getFeedsByUserId(long userId);
    
    void deleteFeedById(long feedId);
    
    void deleteAllFeeds();
    
    boolean isFeedExist(long feedId);
}
