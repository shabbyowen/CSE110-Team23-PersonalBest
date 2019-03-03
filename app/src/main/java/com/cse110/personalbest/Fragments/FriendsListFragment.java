package com.cse110.personalbest.Fragments;

import android.support.v4.app.Fragment;
import com.cse110.personalbest.Events.FriendsListFragmentInfo;
import com.cse110.personalbest.Events.FriendsListFragmentListener;

public abstract class FriendsListFragment extends Fragment {
    public abstract void updateView(FriendsListFragmentInfo info);
    public abstract void setListener(FriendsListFragmentListener listener);
}
