package com.cse110.personalbest.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cse110.personalbest.Events.FriendsListFragmentInfo;
import com.cse110.personalbest.Events.FriendsListFragmentListener;
import com.cse110.personalbest.Friend;
import com.cse110.personalbest.PendingRequestsAdapter;
import com.cse110.personalbest.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class BasicFriendsListFragment extends FriendsListFragment {

    WeakReference<FriendsListFragmentListener> listener;

    // ui elements
    private RecyclerView pendingRequestsListView;
    private RecyclerView friendsListView;

    private PendingRequestsAdapter pendingRequestsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_basic_friends_list, container, false);
        pendingRequestsListView = fragmentView.findViewById(R.id.lv_pending);
        friendsListView = fragmentView.findViewById(R.id.lv_friends);

        pendingRequestsAdapter = new PendingRequestsAdapter(getActivity(), new ArrayList<Friend>());
        pendingRequestsListView.setAdapter(pendingRequestsAdapter);

        return fragmentView;
    }

    @Override
    public void updateView(FriendsListFragmentInfo info) {
        pendingRequestsAdapter = new PendingRequestsAdapter(getActivity(), info.friends);
        pendingRequestsListView.setAdapter(pendingRequestsAdapter);
    }

    @Override
    public void setListener(FriendsListFragmentListener listener) {
        this.listener = new WeakReference<>(listener);
    }
}
