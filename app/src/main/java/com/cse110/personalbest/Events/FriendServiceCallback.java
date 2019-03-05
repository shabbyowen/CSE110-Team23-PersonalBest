package com.cse110.personalbest.Events;

import com.cse110.personalbest.Friend;

import java.util.List;

public class FriendServiceCallback {
    public void onPendingRequestsResult(List<Friend> result) {}
    public void onFriendsListResult(List<Friend> result) {}
    public void onAcceptFriendResult(boolean hasAcceptSuccess) {}
    public void onRejectFriendResult(boolean hasRejectSuccess) {}
    public void onRemoveFriendResult(boolean hasRemoveSuccess) {}
}
