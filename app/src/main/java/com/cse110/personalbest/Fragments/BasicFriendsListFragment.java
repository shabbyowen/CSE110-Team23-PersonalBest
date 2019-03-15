package com.cse110.personalbest.Fragments;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.cse110.personalbest.Events.FriendsListFragmentInfo;
import com.cse110.personalbest.Events.FriendsListFragmentListener;
import com.cse110.personalbest.Friend;
import com.cse110.personalbest.FriendsListAdapter;
import com.cse110.personalbest.PendingRequestsAdapter;
import com.cse110.personalbest.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class BasicFriendsListFragment extends FriendsListFragment {
    private static final String TAG = "BasicFriendsListFragment";

    WeakReference<FriendsListFragmentListener> listener;

    // ui elements
    private LinearLayout pendingRequestsLayout;
    private LinearLayout friendsLayout;
    private RecyclerView pendingRequestsListView;
    private RecyclerView friendsListView;

    private PendingRequestsAdapter pendingRequestsAdapter;
    private PendingRequestsAdapter.PendingRequestsAdapterListener pendingRequestsAdapterListener;
    private FriendsListAdapter friendsListAdapter;
    private FriendsListAdapter.FriendsListAdapterListener friendsListAdapterListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_basic_friends_list, container, false);
        pendingRequestsLayout = fragmentView.findViewById(R.id.ll_pending_requests);

        pendingRequestsListView = fragmentView.findViewById(R.id.lv_pending);
        friendsListView = fragmentView.findViewById(R.id.lv_friends);

        pendingRequestsListView.addItemDecoration(new DividerItemDecoration(pendingRequestsListView.getContext(), DividerItemDecoration.VERTICAL));
        friendsListView.addItemDecoration(new DividerItemDecoration(pendingRequestsListView.getContext(), DividerItemDecoration.VERTICAL));

        pendingRequestsAdapterListener = new PendingRequestsAdapter.PendingRequestsAdapterListener() {

            @Override
            public void acceptBtnOnClick(View v, int position) {
                Log.d(TAG, "acceptFriendButtonOnClick at position "+position);
                onAcceptButtonClick(position);
            }

            @Override
            public void rejectBtnOnClick(View v, int position) {
                Log.d(TAG, "rejectFriendButtonOnClick at position "+position);
                onRejectButtonClick(position);
            }
        };

        friendsListAdapterListener = new FriendsListAdapter.FriendsListAdapterListener() {
            @Override
            public void friendListItemOnClick(View v, int position) {
                Log.d(TAG, "friendListItemOnClick at position "+position);
                onFriendListItemClick(position);
            }

            @Override
            public void removeFriendBtnOnClick(View v, int position) {
                Log.d(TAG, "removeFriendButtonOnClick at position "+position);
                onRemoveFriendButtonClick(position);
            }
        };

        pendingRequestsAdapter = new PendingRequestsAdapter(getActivity(), new ArrayList<Friend>(), pendingRequestsAdapterListener);
        pendingRequestsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        pendingRequestsListView.setAdapter(pendingRequestsAdapter);

        friendsListAdapter = new FriendsListAdapter(getActivity(), new ArrayList<>(), friendsListAdapterListener);
        friendsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendsListView.setAdapter(friendsListAdapter);

        return fragmentView;
    }


    @Override
    public void updateView(FriendsListFragmentInfo info) {
        if (!info.pendingFriends.isEmpty()) {
            pendingRequestsLayout.setVisibility(View.VISIBLE);
            pendingRequestsAdapter = new PendingRequestsAdapter(getActivity(), info.pendingFriends, pendingRequestsAdapterListener);
            pendingRequestsListView.setAdapter(pendingRequestsAdapter);
        } else {
            pendingRequestsLayout.setVisibility(View.GONE);
        }
        friendsListAdapter = new FriendsListAdapter(getActivity(), info.friends, friendsListAdapterListener);
        friendsListView.setAdapter(friendsListAdapter);
    }

    @Override
    public void setListener(FriendsListFragmentListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    private void onAcceptButtonClick(int position) {
        FriendsListFragmentListener ref = listener.get();
        if (ref != null) {
            Friend friend = pendingRequestsAdapter.getItem(position);
            Log.d(TAG, "Will accept friend: " + friend.toString());
            ref.onAcceptButtonClicked(friend);
        }
    }

    private void onRejectButtonClick(int position) {
        FriendsListFragmentListener ref = listener.get();
        if (ref != null) {
            Friend friend = pendingRequestsAdapter.getItem(position);
            Log.d(TAG, "Will reject friend: " + friend.toString());
            ref.onRejectButtonClicked(friend);
        }
    }

    private void onEditNicknameButtonClick(int position) {
    }

    private void onRemoveFriendButtonClick(int position) {
        FriendsListFragmentListener ref = listener.get();
        if (ref != null) {
            Friend friend = friendsListAdapter.getItem(position);
            Log.d(TAG, "Will remove friend: " + friend.toString());
            ref.onRemoveButtonClicked(friend);
        }
    }

    private void onFriendListItemClick(int position) {
        FriendsListFragmentListener ref = listener.get();
        if (ref != null) {
            Friend friend = friendsListAdapter.getItem(position);
            Log.d(TAG, "Will open friend's summary: " + friend.toString());
            ref.onFriendItemClicked(friend);
        }
    }
}
