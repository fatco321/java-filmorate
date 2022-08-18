package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.service.serviseinterface.FeedService;
import ru.yandex.practicum.filmorate.storage.feedstorage.storageinterface.FeedDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedServiceDB implements FeedService {
    
    private final FeedDao feedDao;
    
    @Override
    public List<Feed> getFeedsByUserId(long userId) {
        return feedDao.getUserFeeds(userId);
    }
    
    @Override
    public Feed createFeed(Feed feed) {
        return feedDao.createFeed(feed);
    }
    
    @Override
    public void updateFeed(long feedId) {
    
    }
    
    @Override
    public Feed getFeedById(long feedId) {
        return null;
    }
    
    @Override
    public void deleteFeedById(long feedId) {
    
    }
    
    @Override
    public void deleteAllFeeds() {
    
    }
    
    @Override
    public boolean isFeedExist(long feedId) {
        return false;
    }
}
