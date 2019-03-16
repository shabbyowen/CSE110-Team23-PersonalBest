package com.cse110.personalbest.Fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cse110.personalbest.Events.DailyGoalFragmentInfo;
import com.cse110.personalbest.Events.DailyGoalFragmentListener;
import com.cse110.personalbest.R;

import java.lang.ref.WeakReference;

public class BasicDailyGoalFragment extends DailyGoalFragment {

    // listener
    WeakReference<DailyGoalFragmentListener> listener;

    private boolean viewReady = false;

    // UI elements
    private Button recordBtn;
    private Button changeGoalBtn;
    private Button addStepsBtn;
    private TextView currentStepTextView;
    private TextView currentStepGoalTextView;
    private TextView sessionStepTextView;
    private TextView sessionTimeTextView;
    private TextView sessionSpeedTextView;
    private TextView currentDistTextView;
    private TextView currentDistGoalTextView;

    public BasicDailyGoalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_basic_daily_goal, container, false);

        // assigning listeners
        recordBtn = fragmentView.findViewById(R.id.daily_goal_record_btn);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecordBtnClicked();
            }
        });

        changeGoalBtn = fragmentView.findViewById(R.id.daily_goal_change_goal_btn);
        changeGoalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangeGoalBtnClicked();
            }
        });

        addStepsBtn = fragmentView.findViewById(R.id.daily_goal_add_steps);
        addStepsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddStepBtnClicked();
            }
        });

        currentStepTextView = fragmentView.findViewById(R.id.daily_goal_steps_tv);
        currentStepGoalTextView = fragmentView.findViewById(R.id.daily_goal_goal_steps_tv);
        currentDistTextView = fragmentView.findViewById(R.id.daily_goal_dist_tv);
        currentDistGoalTextView = fragmentView.findViewById(R.id.daily_goal_goal_dist_tv);
        sessionTimeTextView = fragmentView.findViewById(R.id.daily_goal_current_time_tv);
        sessionStepTextView = fragmentView.findViewById(R.id.daily_goal_current_step_tv);
        sessionSpeedTextView = fragmentView.findViewById(R.id.daily_goal_current_speed_tv);

        viewReady = true;

        return fragmentView;
    }

    @Override
    public void updateView(DailyGoalFragmentInfo info) {
        if (viewReady && isAdded()) {
            if (info.step != null) {
                currentStepTextView.setText(String.valueOf(info.step));
            }
            if (info.goal != null) {
                currentStepGoalTextView.setText(String.valueOf(info.goal));
            }
            if (info.currentDist != null) {
                currentDistTextView.setText(String.format("%.2f", info.currentDist));
            }
            if (info.goalDist != null) {
                currentDistGoalTextView.setText(String.format("%.2f", info.goalDist));
            }
            if (info.sessionTime != null) {
                sessionTimeTextView.setText(info.sessionTime);
            }
            if (info.sessionStep != null) {
                sessionStepTextView.setText(String.valueOf(info.sessionStep));
            }
            if (info.sessionSpeed != null) {
                sessionSpeedTextView.setText(String.format("%.2f", info.sessionSpeed));
            }
            if (info.isWorkingOut != null) {

                // enable the button
                recordBtn.setEnabled(true);
                if (info.isWorkingOut) {
                    recordBtn.setBackgroundColor(Color.RED);
                    recordBtn.setText(R.string.end_record);
                } else {
                    recordBtn.setBackgroundColor(getResources().getColor(R.color.green));
                    recordBtn.setText(R.string.start_record);
                }
            }
        }
    }

    @Override
    public void setListener(DailyGoalFragmentListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    public void onRecordBtnClicked() {
        DailyGoalFragmentListener ref = listener.get();
        if (ref != null) {

            // lock the button
            recordBtn.setEnabled(false);
            ref.onRecordBtnClicked();
        }
    }

    public void onChangeGoalBtnClicked() {
        DailyGoalFragmentListener ref = listener.get();
        if (ref != null) {
            ref.onChangeGoalBtnClicked();
        }
    }

    public void onAddStepBtnClicked() {
        DailyGoalFragmentListener ref = listener.get();
        if (ref != null) {
            ref.onAddStepBtnClicked();
        }
    }
}
