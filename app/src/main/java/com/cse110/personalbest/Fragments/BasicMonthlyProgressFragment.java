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

import java.util.Arrays;

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
        ft.add(R.id.fragment_basic_monthly_progress, week4);
        ft.add(R.id.fragment_basic_monthly_progress, week3);
        ft.add(R.id.fragment_basic_monthly_progress, week2);
        ft.add(R.id.fragment_basic_monthly_progress, week1);
        ft.commit();

        /* TESTING STUFF WE DELETE THIS LATER :) */
        MonthlyProgressFragmentInfo info = new MonthlyProgressFragmentInfo();
        info.week1Info = new WeeklyProgressFragmentInfo();
        info.week1Info.intentionalSteps = Arrays.asList(3000, 3000, 5000, 8000, 1000, 2000, 5000);
        info.week1Info.unintentionalSteps = Arrays.asList(0, 2000, 1000, 200, 1000, 5000, 1000);
        info.week1Info.weekGoal = Arrays.asList(3000, 3500, 4000, 4500, 5000, 5500, 6000);
        info.week1Info.weekSpeed = Arrays.asList(14, 13, 12, 14, 13, 12, 14);

        info.week2Info = info.week1Info;
        info.week3Info = info.week1Info;
        info.week4Info = info.week1Info;
        updateView(info);
        /* TESTING STUFF WE DELETE THIS LATER :) */

        return view;
    }

    @Override
    public void updateView(MonthlyProgressFragmentInfo info) {
        if (week1 != null)
            week1.updateView(info.week1Info);
        if (week2 != null)
            week2.updateView(info.week2Info);
        if (week3 != null)
            week3.updateView(info.week3Info);
        if (week4 != null)
            week4.updateView(info.week4Info);
    }
}
