package com.cse110.personalbest.Factories;

import android.content.Intent;
import android.os.IBinder;
import com.cse110.personalbest.Events.FriendServiceCallback;
import com.cse110.personalbest.Friend;
import com.cse110.personalbest.Services.FriendService;

public class MockFriendService extends FriendService {
    @Override
    public void getPendingRequests(FriendServiceCallback callback) {

    }

    @Override
    public void getFriendList(FriendServiceCallback callback) {

    }

    @Override
    public void addFriend(Friend friend, FriendServiceCallback callback) {

    }

    @Override
    public void rejectFriend(Friend rejectedfriend, FriendServiceCallback callback) {

    }

    @Override
    public void removeFriend(Friend removedfriend, FriendServiceCallback callback) {

    }

    @Override
    public void sendFriendRequest(String userEmail, FriendServiceCallback callback) {

    }

    @Override
    public void sendMessage(String friendEmail, String message, FriendServiceCallback callback) {

    }

    @Override
    public void retrieveMessage(String friendEmail, FriendServiceCallback callback) {

    }

    @Override
    public void retrieveProgress(String email, FriendServiceCallback callback) {

    }

    @Override
    public boolean hasFriends() {
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
