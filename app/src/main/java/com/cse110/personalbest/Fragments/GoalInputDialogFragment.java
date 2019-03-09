package com.cse110.personalbest.Fragments;

import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cse110.personalbest.Events.GoalInputDialogResult;
import com.cse110.personalbest.Events.InputDialogFragmentListener;
import com.cse110.personalbest.R;

public class GoalInputDialogFragment extends InputDialogFragment {

    private TextView promptTextView;
    private EditText inputEditText;

    @Override
    public View createView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_goal_input_dialog, null);
        promptTextView = view.findViewById(R.id.fragment_goal_input_dialog_tv);
        promptTextView.setText(R.string.change_goal_instruction_initial);
        inputEditText = view.findViewById(R.id.fragment_goal_input_dialog_et);
        inputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        return view;
    }

    @Override
    public String createPositiveBtn() {
        return getString(R.string.ok);
    }

    @Override
    public String createNegativeBtn() {
        return getString(R.string.cancel);
    }

    @Override
    public void onPositiveBtnClicked(DialogInterface dialog, int i) {
        GoalInputDialogResult result = new GoalInputDialogResult();
        String rawInput = inputEditText.getText().toString();
        int goal = 0;
        try {
            goal = Integer.valueOf(rawInput);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        result.goal = goal;
        if (listener != null && listener.onInputDialogResult(result, this)) {
            dialog.dismiss();
        }
    }

    @Override
    public void onNegativeBtnClicked(DialogInterface dialog, int i) {
        dialog.dismiss();
    }

    @Override
    public void setPrompt(String prompt) {
        promptTextView.setText(prompt);
    }
}
