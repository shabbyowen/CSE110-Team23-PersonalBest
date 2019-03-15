package com.cse110.personalbest.Services;

import android.app.Activity;

import com.cse110.personalbest.Events.StepServiceCallback;

public abstract class StepService extends ObservableService{

    public static final String STORAGE_SOLUTION_KEY_EXTRA = "storage_solution_key_extra";

    public abstract void setup(Activity activity);
    public abstract void getTodayStep(StepServiceCallback callback);
    public abstract void addStep(int step);
    public abstract void getStep(int day, StepServiceCallback callback);
    public abstract void setGoal(int goal);
    public abstract void getTodayGoal(StepServiceCallback callback);
    public abstract void getGoal(int day, StepServiceCallback callback);
}
