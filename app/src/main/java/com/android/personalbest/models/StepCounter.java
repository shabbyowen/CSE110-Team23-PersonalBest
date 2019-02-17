package com.android.personalbest.models;


import android.content.Context;
import android.content.SharedPreferences;
import com.android.personalbest.util.TimeMachine;

public class StepCounter extends Model {

    private static final String COUNTER_SHARED_PREF = "personal_best_counter";
    private static final String STEP_COUNT = "step_count";
    private static final String STEP_GOAL = "step_goal";
    private static final String LAST_SAVED_TIME = "saved_time";
    private static StepCounter instance;

    private SharedPreferences sharedPreferences;
    private int step;
    private int goal;
    private int yesterdayStep;
    private long lastSavedTime;

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

    public void setYesterdayStep(int value) {
        yesterdayStep = value;
    }

    public int getYesterdayStep() {
        return yesterdayStep;
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

    public long getLastSavedTime() {
        return lastSavedTime;
    }

    public void save() {
        sharedPreferences.edit()
                .putInt(STEP_COUNT, step)
                .putInt(STEP_GOAL, goal)
                .putLong(LAST_SAVED_TIME, TimeMachine.nowMillis())
                .apply();
    }

    public void load() {
        step = sharedPreferences.getInt(STEP_COUNT, 0);
        goal = sharedPreferences.getInt(STEP_GOAL, 5000);
        lastSavedTime = sharedPreferences.getLong(LAST_SAVED_TIME, 0);
    }
}
