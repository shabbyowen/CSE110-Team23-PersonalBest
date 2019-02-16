package com.android.personalbest.models;

import android.content.Context;

public class EncouragmentTracker extends Model {


    private static EncouragmentTracker instance;

    public static EncouragmentTracker getInstance(Context context) {
        if (instance == null) {
            instance = new EncouragmentTracker(context);
        }
        return instance;
    }

    public EncouragmentTracker(Context context) {

    }
}
