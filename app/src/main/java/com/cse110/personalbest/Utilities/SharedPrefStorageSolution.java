package com.cse110.personalbest.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefStorageSolution implements StorageSolution {

    private static final String SHARED_PREF_NAME = "shared_pref_storage_solution";
    private static final String TAG = "SharedPrefStorageSolution";

    private SharedPreferences sp;

    public SharedPrefStorageSolution(Context context) {
        sp = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void put(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    @Override
    public String get(String key, String defaultVal) {
        return sp.getString(key, defaultVal);
    }

    @Override
    public void put(String key, Long value) {
        sp.edit().putLong(key, value).apply();
    }

    @Override
    public Long get(String key, Long defaultVal) {
        return sp.getLong(key, defaultVal);
    }

    @Override
    public void put(String key, Integer value) {
        sp.edit().putInt(key, value).apply();
    }

    @Override
    public Integer get(String key, Integer defaultVal) {
        return sp.getInt(key, defaultVal);
    }
}
