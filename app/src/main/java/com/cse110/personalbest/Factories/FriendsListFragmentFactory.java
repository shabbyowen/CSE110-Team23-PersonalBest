package com.cse110.personalbest.Factories;

import android.support.v4.app.Fragment;
import com.cse110.personalbest.Fragments.BasicDailyGoalFragment;
import com.cse110.personalbest.Fragments.BasicFriendsListFragment;

public class FriendsListFragmentFactory implements FragmentFactory {

    public static final String BASIC_FRIENDS_LIST_FRAGMENT_KEY = "basic_friends_list_fragment_key";

    @Override
    public Fragment create(String key) {
        switch (key) {
            case BASIC_FRIENDS_LIST_FRAGMENT_KEY:
                return new BasicFriendsListFragment();
            default:
                return null;
        }
    }
}
