package com.cse110.personalbest.Fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cse110.personalbest.Events.MonthlyProgressFragmentInfo;
import com.cse110.personalbest.Events.WeeklyProgressFragmentInfo;
import com.cse110.personalbest.Factories.WeeklyProgressFragmentFactory;
import com.cse110.personalbest.R;

public class BasicMonthlyProgressFragment extends MonthlyProgressFragment {
    WeeklyProgressFragment  week1,
                            week2,
                            week3,      // Last week
                            week4;      // This week

    public BasicMonthlyProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic_monthly_progress, container, false);

        WeeklyProgressFragmentFactory factory = new WeeklyProgressFragmentFactory();
        week1 = (WeeklyProgressFragment) factory.create(WeeklyProgressFragmentFactory.BASIC_WEEKLY_PROGRESS_FRAGMENT_KEY);
        week2 = (WeeklyProgressFragment) factory.create(WeeklyProgressFragmentFactory.BASIC_WEEKLY_PROGRESS_FRAGMENT_KEY);
        week3 = (WeeklyProgressFragment) factory.create(WeeklyProgressFragmentFactory.BASIC_WEEKLY_PROGRESS_FRAGMENT_KEY);
        week4 = (WeeklyProgressFragment) factory.create(WeeklyProgressFragmentFactory.BASIC_WEEKLY_PROGRESS_FRAGMENT_KEY);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        //currentFragment = dailyGoalFragment;
        ft.add(R.id.fragment_basic_monthly_progress, week4);
        ft.add(R.id.fragment_basic_monthly_progress, week3);
        ft.add(R.id.fragment_basic_monthly_progress, week2);
        ft.add(R.id.fragment_basic_monthly_progress, week1);
        ft.commit();

        return view;
    }

    @Override
    public void updateView(MonthlyProgressFragmentInfo info) {
        week1.updateView(info.week1Info);
        week2.updateView(info.week2Info);
        week3.updateView(info.week3Info);
        week4.updateView(info.week4Info);
    }
}
