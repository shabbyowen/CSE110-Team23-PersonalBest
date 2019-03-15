package com.cse110.personalbest.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.cse110.personalbest.ChatMessage;
import com.cse110.personalbest.Events.FriendServiceCallback;
import com.cse110.personalbest.Events.MyBinder;
import com.cse110.personalbest.Events.WeeklyProgressFragmentInfo;
import com.cse110.personalbest.Friend;

import java.util.ArrayList;
import java.util.List;

public class MockFriendService extends FriendService {
    public static final String STORAGE_SOLUTION_KEY_EXTRA = "storage_solution_key_extra";
    public static final String FRIEND_SERVICE_KEY_EXTRA = "friend_service_key_extra";
    private final IBinder binder = new MockSessionServiceBinder();

    public class MockSessionServiceBinder extends MyBinder {
        @Override
        public Service getService() {
                      return MockFriendService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void getPendingRequests(FriendServiceCallback callback){

    }
    public void getFriendList(FriendServiceCallback callback){
        List<Friend> friendsList = new ArrayList<>();
        friendsList.add(new Friend("yal272@ucsd.edu"));
        friendsList.add(new Friend("jit072@ucsd.edu"));
        callback.onFriendsListResult(friendsList);
    }
    public void addFriend(Friend friend, FriendServiceCallback callback){

    }
    public void rejectFriend(Friend rejectedfriend, FriendServiceCallback callback){

    }
    public void removeFriend(Friend removedfriend, FriendServiceCallback callback){

    }
    public void sendFriendRequest(String userEmail, FriendServiceCallback callback){

    }
    public void sendMessage(String friendEmail, String message, FriendServiceCallback callback){

    }
    public void retrieveMessage(String friendEmail, FriendServiceCallback callback){
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("yal272@ucsd.edu",
            "LOL I'm here", "18:30", ChatMessage.MSG_TYPE.TO_FRIEND));
        messages.add(new ChatMessage("yal272@ucsd.edu",
            "Where are you?", "18:32", ChatMessage.MSG_TYPE.TO_FRIEND));
        messages.add(new ChatMessage("yal272@ucsd.edu",
            "Here", "18:32", ChatMessage.MSG_TYPE.FROM_FRIEND));
        callback.onRetrieveMessageResult(messages);
    }
    public void retrieveProgress(String email, FriendServiceCallback callback){

        WeeklyProgressFragmentInfo weeklyProgressInfo = new WeeklyProgressFragmentInfo();
        List<Integer> weekSpeed = new ArrayList<>(); //[25, 24, 23, 22, 21, 20, 19]
        List<Integer> intentionalStep = new ArrayList<>(); //[500, 490, 480, 470, 460, 450, 440]
        List<Integer> unintentionalStep = new ArrayList<>(); //[600, 590, 580, 570, 560, 550, 540]
        List<Integer> weekGoal = new ArrayList<>(); //[1000, 1000, 1000, 1000, 1000, 1000, 1000]
        for(int i  = 0; i < 7; i++){
            weekSpeed.add(25 - i);
            intentionalStep.add(500 - i*10);
            unintentionalStep.add(600 - i*10);
            weekGoal.add(1000);

        }

        weeklyProgressInfo.intentionalSteps = intentionalStep;
        weeklyProgressInfo.weekSpeed = weekSpeed;
        weeklyProgressInfo.unintentionalSteps = unintentionalStep;
        weeklyProgressInfo.weekGoal = weekGoal;

        callback.onRetrieveProgressResult(weeklyProgressInfo);
    }
}
