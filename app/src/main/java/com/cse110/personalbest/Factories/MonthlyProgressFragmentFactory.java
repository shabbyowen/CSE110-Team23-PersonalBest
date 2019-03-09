package com.cse110.personalbest.Factories;

import android.support.v4.app.Fragment;

import com.cse110.personalbest.Fragments.BasicMonthlyProgressFragment;

public class MonthlyProgressFragmentFactory implements FragmentFactory {

    public static final String BASIC_WEEKLY_PROGRESS_FRAGMENT_KEY = "basic_monthly_progress_fragment_key";

    @Override
    public Fragment create(String key) {
        switch (key) {
            case BASIC_WEEKLY_PROGRESS_FRAGMENT_KEY:
                return new BasicMonthlyProgressFragment();
            default:
                return null;
        }
    }
}
