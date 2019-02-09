package com.android.personalbest;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class DailyGoalFragment extends Fragment {

    private Activity parentActivity;
    private Button recordBtn;

    public DailyGoalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_goal, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        parentActivity = getActivity();
        recordBtn = parentActivity.findViewById(R.id.daily_goal_record_btn);
        recordBtn.setOnClickListener(this::onRecordBtnClicked);
    }

    public void onRecordBtnClicked(View view) {

    }
}
