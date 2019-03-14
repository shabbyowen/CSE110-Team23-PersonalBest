package com.cse110.personalbest.Activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cse110.personalbest.Events.MonthlyProgressFragmentInfo;
import com.cse110.personalbest.Events.WeeklyProgressFragmentInfo;
import com.cse110.personalbest.Factories.MonthlyProgressFragmentFactory;
import com.cse110.personalbest.Fragments.MonthlyProgressFragment;
import com.cse110.personalbest.R;

import java.util.Arrays;

public class MonthlyHistoryActivity extends AppCompatActivity {
    MonthlyProgressFragment monthlyProgressFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_history);

        monthlyProgressFragment = (MonthlyProgressFragment) new MonthlyProgressFragmentFactory()
                .create(MonthlyProgressFragmentFactory.BASIC_WEEKLY_PROGRESS_FRAGMENT_KEY);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.monthly_history_container, monthlyProgressFragment);
        ft.commit();

        //updateMonthlyProgressFragment();
    }

    public void updateMonthlyProgressFragment() {
        MonthlyProgressFragmentInfo info = new MonthlyProgressFragmentInfo();

        info.week1Info = new WeeklyProgressFragmentInfo();
        info.week1Info.intentionalSteps = Arrays.asList(3000, 3000, 5000, 8000, 1000, 2000, 5000);
        info.week1Info.unintentionalSteps = Arrays.asList(0, 2000, 1000, 200, 1000, 5000, 1000);
        info.week1Info.weekGoal = Arrays.asList(3000, 3500, 4000, 4500, 5000, 5500, 6000);
        info.week1Info.weekSpeed = Arrays.asList(14, 13, 12, 14, 13, 12, 14);

        info.week2Info = info.week1Info;
        info.week3Info = info.week1Info;
        info.week4Info = info.week1Info;

        /*
        info.week2Info = new WeeklyProgressFragmentInfo();
        info.week2Info.intentionalSteps = Arrays.asList();
        info.week2Info.unintentionalSteps = Arrays.asList();
        info.week2Info.weekGoal = Arrays.asList();
        info.week2Info.weekSpeed = Arrays.asList();

        info.week3Info = new WeeklyProgressFragmentInfo();
        info.week3Info.intentionalSteps = Arrays.asList();
        info.week3Info.unintentionalSteps = Arrays.asList();
        info.week3Info.weekGoal = Arrays.asList();
        info.week3Info.weekSpeed = Arrays.asList();

        info.week4Info = new WeeklyProgressFragmentInfo();
        info.week4Info.intentionalSteps = Arrays.asList();
        info.week4Info.unintentionalSteps = Arrays.asList();
        info.week4Info.weekGoal = Arrays.asList();
        info.week4Info.weekSpeed = Arrays.asList();
        */
    }
}
