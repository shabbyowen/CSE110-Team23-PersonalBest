package com.android.personalbest.models;


import android.content.Context;
import android.content.SharedPreferences;

public class StepCounter extends Model {

    private static final String COUNTER_SHARED_PREF = "personal_best_counter";
    private static final String STEP_COUNT = "step_count";
    private static final String STEP_GOAL = "step_goal";
    private static StepCounter instance;

    private SharedPreferences sharedPreferences;
    private int step;
    private int goal;

    public static StepCounter getInstance(Context context) {
        if (instance == null) {
            instance = new StepCounter(context);
        }
        return instance;
    }

    public class Result {

        public int step;
        public int goal;

        public Result(int step, int goal){
            this.step = step;
            this.goal = goal;
        }
    }

    public StepCounter(Context context) {
        super();
        sharedPreferences = context.getSharedPreferences(COUNTER_SHARED_PREF, Context.MODE_PRIVATE);
        load();
    }

    public void setStep(int value) {
        step = value;
        updateAll();
    }

    public void setGoal(int value) {
        goal = value;
        updateAll();
    }

    public void updateAll() {
        update(new Result(step, goal));
    }

    public int getStep() {
        return step;
    }

    public int getGoal() {
        return goal;
    }

    public void save() {
        sharedPreferences.edit().putInt(STEP_COUNT, step).putInt(STEP_GOAL, goal).apply();
    }

    public void load() {
        step = sharedPreferences.getInt(STEP_COUNT, 0);
        goal = sharedPreferences.getInt(STEP_GOAL, 5000);
    }
}
