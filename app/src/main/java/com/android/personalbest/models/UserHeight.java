package com.android.personalbest.models;

import android.content.Context;
import android.content.SharedPreferences;
import com.android.personalbest.HeightPromptFragment;

public class UserHeight {

    private static final String HEIGHT_SHARED_PREF = HeightPromptFragment.HEIGHT_SHARED_PREF;
    private static final String HEIGHT = HeightPromptFragment.HEIGHT;
    private static UserHeight instance;

    private SharedPreferences sharedPreferences;
    private int height;

    public static UserHeight getInstance(Context context) {
        if (instance == null) {
            instance = new UserHeight(context);
        }
        return instance;
    }

    public static UserHeight getInstance() {
        return instance;
    }

    public UserHeight(Context context) {
        this.sharedPreferences = context.getSharedPreferences(HEIGHT_SHARED_PREF, Context.MODE_PRIVATE);
        load();
    }

    public int getHeight() {
        return height;
    }

    public void load() {
        height = sharedPreferences.getInt(HEIGHT, 0);
    }

    public void save() {
        sharedPreferences.edit().putInt(HEIGHT, height).apply();
    }
}
