package com.android.personalbest.models;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class StepCounter {

    private static final String USER_SHARED_PREF = "personal_best_user";
    private static final String STEP_COUNT = "step_count";
    private static final String STEP_GOAL = "step_goal";

    private static StepCounter instance;

    public static StepCounter getInstance(Context context) {
        if (instance == null) {
            instance = new StepCounter(context);
        }
        return instance;
    }

    private SharedPreferences sharedPreferences;
    private List<Listener> listeners;

    public interface Listener {
        void onStepChanged(int value);
        void onGoalChanged(int value);
    }

    public StepCounter(Context context) {
        this.sharedPreferences = context.getSharedPreferences(USER_SHARED_PREF, Context.MODE_PRIVATE);
        listeners = new ArrayList<>();
    }

    public void addListener(Listener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void setStep(int value) {
        sharedPreferences.edit().putInt(STEP_COUNT, value).apply();
        for (Listener listener: listeners) {
            listener.onStepChanged(value);
        }
    }

    public void setGoal(int value) {
        sharedPreferences.edit().putInt(STEP_GOAL, value).apply();
        for (Listener listener: listeners) {
            listener.onGoalChanged(value);
        }
    }

    public int getStep() {
        return sharedPreferences.getInt(STEP_COUNT, -1);
    }

    public int getGoal() {
        return sharedPreferences.getInt(STEP_GOAL, 5000);
    }
}
