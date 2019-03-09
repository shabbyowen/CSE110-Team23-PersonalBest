package com.cse110.personalbest.Events;

import com.cse110.personalbest.Friend;

import java.util.List;

public class FriendServiceCallback {
    public static final int NO_INTERNET_CONNECTION = -1;
    public static final int OPERATION_SUCCESS = 0;
    public static final int USER_DOES_NOT_EXIST = 1;
    public static final int USER_ALREADY_FRIEND = 2;
    public void onPendingRequestsResult(List<Friend> result) {}
    public void onFriendsListResult(List<Friend> result) {}
    public void onAcceptFriendResult(boolean hasAcceptSuccess) {}
    public void onRejectFriendResult(boolean hasRejectSuccess) {}
    public void onRemoveFriendResult(boolean hasRemoveSuccess) {}
    public void onSendFriendRequestResult(int statusCode) {}
    public void onSendMessageResult(boolean hasSendMessageSuccess) {}
    public void onRetrieveMessageResult() {}
}
