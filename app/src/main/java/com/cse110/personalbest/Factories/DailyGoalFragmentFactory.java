package com.cse110.personalbest.Factories;

import android.support.v4.app.Fragment;

import com.cse110.personalbest.Fragments.BasicDailyGoalFragment;

public class DailyGoalFragmentFactory implements FragmentFactory {

    public static final String BASIC_DAILY_GOAL_FRAGMENT_KEY = "basic_daily_goal_fragment_key";

    @Override
    public Fragment create(String key) {
        switch (key) {
            case BASIC_DAILY_GOAL_FRAGMENT_KEY:
                return new BasicDailyGoalFragment();
            default:
                return null;
        }
    }
}
