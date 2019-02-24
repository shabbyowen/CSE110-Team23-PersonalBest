package com.cse110.personalbest.Factories;

import android.support.v4.app.Fragment;

import com.cse110.personalbest.Fragments.BasicWeeklyProgressFragment;

public class WeeklyProgressFragmentFactory implements FragmentFactory {

    public static final String BASIC_WEEKLY_PROGRESS_FRAGMENT_KEY = "basic_weekly_progres_fragment_key";

    @Override
    public Fragment create(String key) {
        switch (key) {
            case BASIC_WEEKLY_PROGRESS_FRAGMENT_KEY:
                return new BasicWeeklyProgressFragment();
            default:
                return null;
        }
    }
}
