package com.cse110.personalbest.Factories;

import android.support.v4.app.Fragment;

import com.cse110.personalbest.Fragments.GoalInputDialogFragment;
import com.cse110.personalbest.Fragments.GoalMetInputDialogFragment;
import com.cse110.personalbest.Fragments.HeightInputDialogFragment;
import com.cse110.personalbest.Fragments.InputDialogFragment;

public class InputDialogFragmentFactory implements FragmentFactory {

    public static final String GOAL_INPUT_DIALOG_FRAGMENT_KEY = "goal_input_fragment_dialog_key";
    public static final String HEIGHT_INPUT_DIALOG_FRAGMENT_KEY = "height_input_fragment_dialog_key";
    public static final String GOAL_MET_INPUT_DIALOG_FRAGMENT_KEY = "goal_met_input_dialog_fragment_key";

    @Override
    public Fragment create(String key) {
        switch (key) {
            case GOAL_INPUT_DIALOG_FRAGMENT_KEY:
                return new GoalInputDialogFragment();
            case HEIGHT_INPUT_DIALOG_FRAGMENT_KEY:
                return new HeightInputDialogFragment();
            case GOAL_MET_INPUT_DIALOG_FRAGMENT_KEY:
                return new GoalMetInputDialogFragment();
            default:
                return null;
        }
    }
}
