package com.android.personalbest.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;

import com.android.personalbest.fitness.FitnessService;
import com.android.personalbest.util.DateCalculator;
import com.android.personalbest.util.TimeMachine;

import java.util.Date;

public class EncouragementTracker extends Model {

    private static final String ENCOURAGEMENT_SHARED_PREF = "personal_best_encouragement";
    private static final String LAST_GOAL_PROMPT_TIME = "last_goal_prompt_time";
    private static final String LAST_ENCOURAGEMENT_TIME = "last_encouragement_time";
    private static EncouragementTracker instance;

    private SharedPreferences sharedPreferences;
    private long lastGoalPromptTime;
    private long lastEncouragementTime;
    private FitnessService fitnessService;
    private Context context;

    public static EncouragementTracker getInstance(Context context) {
        if (instance == null) {
            instance = new EncouragementTracker(context);
        }
        return instance;
    }

    public void setFitnessService(FitnessService fitnessService) {
        this.fitnessService = fitnessService;
    }

    public EncouragementTracker(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(ENCOURAGEMENT_SHARED_PREF, Context.MODE_PRIVATE);
        load();
    }

    public void setLastGoalPromptTime(long lastGoalPromptTime) {
        this.lastGoalPromptTime = lastGoalPromptTime;
    }

    public void setLastEncouragementTime(long lastEncouragementTime) {
        this.lastEncouragementTime = lastEncouragementTime;
    }

    public boolean shouldDisplayGoalPrompt() {
        boolean result = DateCalculator.dateChanged(lastGoalPromptTime, TimeMachine.nowMillis());
        return result;
    }

    public boolean shouldDisplayEncouragement() {
        boolean result = DateCalculator.dateChanged(lastEncouragementTime, TimeMachine.nowMillis());
        return result;
    }

    public void load() {
        lastGoalPromptTime = sharedPreferences.getLong(LAST_GOAL_PROMPT_TIME, 0);
        lastEncouragementTime = sharedPreferences.getLong(LAST_ENCOURAGEMENT_TIME, 0);
    }

    public void save() {
        sharedPreferences.edit()
            .putLong(LAST_GOAL_PROMPT_TIME, lastGoalPromptTime)
            .putLong(LAST_ENCOURAGEMENT_TIME, lastEncouragementTime)
            .apply();
    }
}
