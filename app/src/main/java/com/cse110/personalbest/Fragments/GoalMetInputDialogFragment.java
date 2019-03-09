package com.cse110.personalbest.Fragments;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cse110.personalbest.Events.GoalMetInputDialogResult;
import com.cse110.personalbest.R;

public class GoalMetInputDialogFragment extends InputDialogFragment {


    @Override
    public View createView(LayoutInflater inflater) {

        // inflate the view
        View view = inflater.inflate(R.layout.fragment_goal_met_input_dialog, null);
        ((TextView) view.findViewById(R.id.fragment_goal_met_input_dialog_tv))
            .setText(R.string.met_goal);
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
        GoalMetInputDialogResult result = new GoalMetInputDialogResult();
        result.good = true;
        listener.onInputDialogResult(result, null);
        dialog.dismiss();
    }

    @Override
    public void onNegativeBtnClicked(DialogInterface dialog, int i) {
        GoalMetInputDialogResult result = new GoalMetInputDialogResult();
        result.good = false;
        listener.onInputDialogResult(result, null);
        dialog.dismiss();
    }

    @Override
    public void setPrompt(String prompt) {
        // does nothing
    }
}
