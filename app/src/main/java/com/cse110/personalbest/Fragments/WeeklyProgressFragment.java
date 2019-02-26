package com.cse110.personalbest.Fragments;

import android.support.v4.app.Fragment;
import com.cse110.personalbest.Events.WeeklyProgressFragmentInfo;

public abstract class WeeklyProgressFragment extends Fragment {

    public abstract void updateView(WeeklyProgressFragmentInfo info);
}
