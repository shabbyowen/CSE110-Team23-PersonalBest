package com.cse110.personalbest.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.cse110.personalbest.Events.FriendsListFragmentInfo;
import com.cse110.personalbest.Events.FriendsListFragmentListener;
import com.cse110.personalbest.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.WeakReference;

public class BasicFriendsListFragment extends FriendsListFragment {
    private static final String COLLECTION_KEY = "users";
    private static final String FRIENDS_KEY = "friends";
    private static final String PENDING_REQUESTS_KEY = "pending_requests";

    WeakReference<FriendsListFragmentListener> listener;

    private DocumentReference user;

    // ui elements
    private ListView pendingRequestsListView;
    private ListView friendsListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_basic_friends_list, container, false);
        pendingRequestsListView = fragmentView.findViewById(R.id.lv_pending);
        friendsListView = fragmentView.findViewById(R.id.lv_friends);

        // Get data from Firebase
        user = FirebaseFirestore.getInstance().
        return fragmentView;
    }

    @Override
    public void updateView(FriendsListFragmentInfo info) {

    }

    @Override
    public void setListener(FriendsListFragmentListener listener) {
        this.listener = new WeakReference<>(listener);
    }
}
