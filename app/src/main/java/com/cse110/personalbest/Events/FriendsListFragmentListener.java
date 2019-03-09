package com.cse110.personalbest.Events;

import com.cse110.personalbest.Friend;

public interface FriendsListFragmentListener {
    void onAcceptButtonClicked(Friend friend);
    void onRejectButtonClicked(Friend rejectedFriend);
    void onEditNicknameButtonClicked(Friend friend);
    void onRemoveButtonClicked(Friend removedFriend);
    void onFriendItemClicked(Friend friend);
}
