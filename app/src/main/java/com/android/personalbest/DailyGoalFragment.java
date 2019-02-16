package com.android.personalbest;


import android.icu.text.StringPrepParseException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.android.personalbest.models.Model;
import com.android.personalbest.models.StepCounter;
import com.android.personalbest.models.WorkoutRecord;
import com.android.personalbest.util.SpeedCalculator;
import com.android.personalbest.util.TimeMachine;

public class DailyGoalFragment extends Fragment implements
    InputDialogFragment.InputDialogListener,
    Model.Listener {

    private static final String TAG = "DailyGoalFragment";

    // use this tag to identify the source of onInputResult
    private static final String SET_GOAL = "set_goal";
    private static final String ADD_STEP = "add_step";

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

    // models
    private StepCounter counter;
    private WorkoutRecord record;

    // Required empty public constructor
    public DailyGoalFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize the model field
        counter = StepCounter.getInstance(getContext());
        counter.addListener(this);
        record = WorkoutRecord.getInstance(getContext());
        record.addListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_daily_goal, container, false);

        // assigning listeners
        recordBtn = fragmentView.findViewById(R.id.daily_goal_record_btn);
        recordBtn.setOnClickListener(this::onRecordBtnClicked);

        changeGoalBtn = fragmentView.findViewById(R.id.daily_goal_change_goal_btn);
        changeGoalBtn.setOnClickListener(this::onChangeGoalBtnClicked);

        currentStepTextView = fragmentView.findViewById(R.id.daily_goal_steps_tv);
        currentStepGoalTextView = fragmentView.findViewById(R.id.daily_goal_goal_steps_tv);
        currentDistTextView = fragmentView.findViewById(R.id.daily_goal_dist_tv);
        currentDistGoalTextView = fragmentView.findViewById(R.id.daily_goal_goal_dist_tv);
        sessionTimeTextView = fragmentView.findViewById(R.id.daily_goal_current_time_tv);
        sessionStepTextView = fragmentView.findViewById(R.id.daily_goal_current_step_tv);
        sessionSpeedTextView = fragmentView.findViewById(R.id.daily_goal_current_speed_tv);

        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        updateCurrentStepAndGoal(counter.getStep(), counter.getGoal());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // clean up
        counter.removeListener(this);
        record.removeListener(this);
    }

    // utility functions

    private void updateCurrentStepAndGoal(int step, int goal) {

        // display step goal and current steps
        currentStepTextView.setText(String.valueOf(step));
        currentStepGoalTextView.setText(String.valueOf(goal));
        double goalDist = SpeedCalculator.stepToMiles(goal);
        double currDist = SpeedCalculator.stepToMiles(step);
        currentDistTextView.setText(String.format("%.2f", currDist));
        currentDistGoalTextView.setText(String.format("%.2f", goalDist));
    }

    private String formatTime(int second) {
        int hour = 0;
        while (second >= 3600) {
            hour++;
            second -= 3600;
        }
        int minute = 0;
        while (second >= 60) {
            minute++;
            second -= 60;
        }
        StringBuilder sb = new StringBuilder();
        padZero(sb, hour);
        sb.append(':');
        padZero(sb, minute);
        sb.append(':');
        padZero(sb, second);
        return sb.toString();
    }

    private void padZero(StringBuilder sb, int number) {
        if (number == 0) {
            sb.append("00");
            return;
        }
        if (number < 10) {
            sb.append(0);
        }
        sb.append(number);
    }

    // event listeners

    public void onRecordBtnClicked(View view) {
        if (!record.isWorkingout()) {
            record.startWorkout(TimeMachine.nowMillis(), counter.getStep());
            recordBtn.setText(R.string.end_record);
        } else {
            record.endWorkout();
            recordBtn.setText(R.string.start_record);
        }
    }

    public void onChangeGoalBtnClicked(View view) {
        DialogFragment dialog = InputDialogFragment.newInstance(this, SET_GOAL, R.string.change_goal_instruction_initial);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        dialog.show(fm, "input_fragment");
    }

    public void onAddStepsBtnClicked(View view) {
        DialogFragment dialog = InputDialogFragment.newInstance(this, ADD_STEP, R.string.add_step_instruction_initial);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        dialog.show(fm, "input_fragment");
    }

    @Override
    public boolean onInputResult(String tag, String result, TextView prompt) {
        // processes the result from the input dialog
        switch (tag) {

            // from set goal
            case SET_GOAL:

                // validate entered goal
                int value;
                try {
                    value = Integer.valueOf(result);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return false;
                }
                if (value > 0) {
                    counter.setGoal(value);
                    counter.save();
                    Toast.makeText(getActivity(), R.string.saved, Toast.LENGTH_LONG).show();
                    Log.d(TAG, String.format("changing daily goal to %d", value));
                    return true;
                } else {
                    prompt.setText(R.string.change_goal_instruction_failed);
                    return false;
                }
            default:
                return false;
        }
    }


    @Override
    public void onUpdate(Object o) {

        // counter results
        if (o instanceof StepCounter.Result) {
            StepCounter.Result result = (StepCounter.Result) o;
            updateCurrentStepAndGoal(result.step, result.goal);

        // record results
        } else if (o instanceof WorkoutRecord.Result) {
            WorkoutRecord.Result result = (WorkoutRecord.Result) o;
            sessionStepTextView.setText(String.valueOf(result.deltaStep));
            sessionTimeTextView.setText(formatTime(result.deltaTime));
            double mph = SpeedCalculator.calculateSpeed(result.deltaStep, result.deltaTime);
            sessionSpeedTextView.setText(String.format("%.2f", mph));
        }
    }
}
