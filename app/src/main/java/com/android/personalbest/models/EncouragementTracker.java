package com.android.personalbest.models;

import android.content.Context;
<<<<<<< HEAD

public class EncouragmentTracker extends Model {


    private static EncouragmentTracker instance;

    public static EncouragmentTracker getInstance(Context context) {
        if (instance == null) {
            instance = new EncouragmentTracker(context);
=======
import android.content.SharedPreferences;

public class EncouragementTracker extends Model {

    private static final String ENCOURAGEMENT_SHARED_PREF = "personal_best_encouragement";
    private static final String HAS_DISPLAYED_ENCOURAGMENT = "has_displayed_encouragement";
    private static final String LAST_TIME_ENCOURAGMENT = "last_time_encouragement";
    private static EncouragementTracker instance;

    private SharedPreferences sharedPreferences;
    private long lastEncouragement;
    private boolean encouragementDisplayed;

    public static EncouragementTracker getInstance(Context context) {
        if (instance == null) {
            instance = new EncouragementTracker(context);
>>>>>>> 1ed20eddfcb635f9d6de1d6761f62419c43ad431
        }
        return instance;
    }

<<<<<<< HEAD
    public EncouragmentTracker(Context context) {

=======
    public EncouragementTracker(Context context) {
        sharedPreferences = context.getSharedPreferences(ENCOURAGEMENT_SHARED_PREF, Context.MODE_PRIVATE);
        //load();
    }

    public void load() {
        lastEncouragement = sharedPreferences.getLong(LAST_TIME_ENCOURAGMENT, 0);
        encouragementDisplayed = sharedPreferences.getBoolean(HAS_DISPLAYED_ENCOURAGMENT, false);
>>>>>>> 1ed20eddfcb635f9d6de1d6761f62419c43ad431
    }
}
