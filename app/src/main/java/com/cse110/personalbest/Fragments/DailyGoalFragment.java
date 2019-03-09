package com.cse110.personalbest.Fragments;

import android.support.v4.app.Fragment;

import com.cse110.personalbest.Events.DailyGoalFragmentInfo;
import com.cse110.personalbest.Events.DailyGoalFragmentListener;

public abstract class DailyGoalFragment extends Fragment {

    public abstract void updateView(DailyGoalFragmentInfo info);
    public abstract void setListener(DailyGoalFragmentListener listener);
}
