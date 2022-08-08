package ru.yandex.practicum.filmorate.storage.userstorage.storageinterface;

import java.util.List;

public interface FriendsDao {
    List<Long> getFriends(long userId);

    void addFriend(long userId, long friendUserId);

    void deleteFriend(long userId, long friendUserId);

    List<Long> getUserAllFriendsId(long userId);
}
