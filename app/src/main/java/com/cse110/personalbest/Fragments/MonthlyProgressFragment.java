package com.cse110.personalbest.Fragments;

import android.support.v4.app.Fragment;

import com.cse110.personalbest.Events.MonthlyProgressFragmentInfo;

public abstract class MonthlyProgressFragment extends Fragment {
    public abstract void updateView(MonthlyProgressFragmentInfo info);
}
