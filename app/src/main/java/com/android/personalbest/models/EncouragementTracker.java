package com.android.personalbest.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.personalbest.util.DateCalculator;
import com.android.personalbest.util.TimeMachine;

public class EncouragementTracker extends Model {

    private static final String ENCOURAGEMENT_SHARED_PREF = "personal_best_encouragement";
    private static final String LAST_GOAL_PROMPT_TIME = "last_goal_prompt_time";
    private static EncouragementTracker instance;

    private SharedPreferences sharedPreferences;
    private long lastGoalPromptTime;

    public static EncouragementTracker getInstance(Context context) {
        if (instance == null) {
            instance = new EncouragementTracker(context);
            instance = new EncouragementTracker(context);
        }
        return instance;
    }

    public EncouragementTracker(Context context) {
        sharedPreferences = context.getSharedPreferences(ENCOURAGEMENT_SHARED_PREF, Context.MODE_PRIVATE);
        load();
    }

    public void setLastGoalPromptTime(long lastGoalPromptTime) {
        this.lastGoalPromptTime = lastGoalPromptTime;
    }

    public boolean shouldDisplayGoalPrompt() {
        boolean result = DateCalculator.dateChanged(lastGoalPromptTime, TimeMachine.nowMillis());
        lastGoalPromptTime = TimeMachine.nowMillis();
        return result;
    }

    public void load() {
        lastGoalPromptTime = sharedPreferences.getLong(LAST_GOAL_PROMPT_TIME, 0);
    }

    public void save() {
        sharedPreferences.edit()
            .putLong(LAST_GOAL_PROMPT_TIME, lastGoalPromptTime)
            .apply();
    }
}
