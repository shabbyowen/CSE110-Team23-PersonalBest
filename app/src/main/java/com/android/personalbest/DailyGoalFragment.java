package com.android.personalbest;


import android.app.Activity;
import android.renderscript.ScriptGroup;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;


import static android.content.Context.MODE_PRIVATE;

public class DailyGoalFragment extends Fragment implements InputDialogFragment.InputDialogListener {

    // use this tag to identify the source of onInputResult
    public static final String SET_GOAL = "set_goal";
    public static final String ADD_STEP = "add_step";

    private Button recordBtn;
    private Button changeGoalBtn;
    private Button addStepsBtn;

    public DailyGoalFragment() {
        // Required empty public constructor
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

        addStepsBtn = fragmentView.findViewById(R.id.daily_goal_add_steps_btn);
        addStepsBtn.setOnClickListener(this::onAddStepsBtnClicked);

        return fragmentView;
    }

    public void onRecordBtnClicked(View view) {

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
                int value;
                try {
                    value = Integer.valueOf(result);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return false;
                }
                if (value > 0) {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("personal_best", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("daily_goal", value);
                    editor.apply();
                    Toast.makeText(getActivity(), R.string.saved, Toast.LENGTH_LONG).show();
                    return true;
                } else {
                    prompt.setText(R.string.change_goal_instruction_failed);
                    return false;
                }

            // from add step
            case ADD_STEP:
                break;
            default:
                return false;
        }
        return false;
    }
}
