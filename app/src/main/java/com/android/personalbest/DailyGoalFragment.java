package com.android.personalbest;


import android.content.DialogInterface;
import android.icu.text.StringPrepParseException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.android.personalbest.models.EncouragementTracker;
import com.android.personalbest.models.Model;
import com.android.personalbest.models.StepCounter;
import com.android.personalbest.models.WorkoutRecord;
import com.android.personalbest.util.DateCalculator;
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
    private EncouragementTracker encouragementTracker;

    // Required empty public constructor
    public DailyGoalFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize the model field
        counter = StepCounter.getInstance(getContext());
        record = WorkoutRecord.getInstance(getContext());
        encouragementTracker = EncouragementTracker.getInstance(getContext());
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

        addStepsBtn = fragmentView.findViewById(R.id.daily_goal_add_steps);
        addStepsBtn.setOnClickListener(this::onAddStepsBtnClicked);

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
    public void onResume() {
        super.onResume();

        // add listener
        counter.addListener(this);
        record.addListener(this);

        // the correct the text of the button
        if (record.isWorkingout()) {
            recordBtn.setText(R.string.end_record);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // clean up
        counter.removeListener(this);
        record.removeListener(this);

        // save the running session
        counter.save();
        record.save();
        encouragementTracker.save();
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

        // check if the user has met the goal
        if (step >= goal && goal < 15000 && encouragementTracker.shouldDisplayGoalPrompt()) {
            Log.d(TAG, "Showing meet goal encouragement");
            encouragementTracker.setLastGoalPromptTime(TimeMachine.nowMillis());
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

            // set dialog message
            alertDialogBuilder
                .setMessage(R.string.met_goal)
                .setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        counter.setGoal(counter.getGoal() + 500);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.dismiss();
                    }
                });
            alertDialogBuilder.create().show();
            return;
        }

        int tmp = counter.getYesterdayStep();
        long tmp2 = TimeMachine.nowMillis();
        long tmp3 = DateCalculator.toLocalTime(TimeMachine.nowMillis());
        long tmp4 = DateCalculator.toLocalTime(TimeMachine.nowMillis()) % (86400 * 1000);
        long tmp5 = 20 * 3600 * 1000;
        boolean tmp6 = encouragementTracker.shouldDisplayEncouragement();
        if (step - counter.getYesterdayStep() >= 500 &&
            DateCalculator.toLocalTime(TimeMachine.nowMillis()) % (86400 * 1000) > 20 * 3600 * 1000 &&
            encouragementTracker.shouldDisplayEncouragement()) {
            Log.d(TAG, "Showing sub-goal encouragement");
            encouragementTracker.setLastEncouragementTime(TimeMachine.nowMillis());
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

            // set dialog message
            alertDialogBuilder
                .setMessage(String.format("Congrats, you walked over %d more steps than yesterday!",
                    (step - counter.getYesterdayStep()) / 500 * 500))
                .setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.dismiss();
                    }
                });
            alertDialogBuilder.create().show();
        }
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
        counter.setStep(counter.getStep() + 500);
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
            sessionTimeTextView.setText(formatTime(result.deltaTime / 1000));
            double mph = SpeedCalculator.calculateSpeed(result.deltaStep, result.deltaTime);
            sessionSpeedTextView.setText(String.format("%.2f", mph));
        }
    }
}
