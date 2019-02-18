package com.android.personalbest.fitness;

import android.util.Log;

import com.android.personalbest.HomeScreenActivity;
import com.android.personalbest.fitness.FitnessService;

import java.util.HashMap;
import java.util.Map;



public class FitnessServiceFactory {

    private static final String TAG = "[FitnessServiceFactory]";

    private static Map<String, BluePrint> blueprints = new HashMap<>();

    public static void put(String key, BluePrint bluePrint) {
        blueprints.put(key, bluePrint);
    }

    public static FitnessService create(String key, HomeScreenActivity homeScreenActivity) {
        Log.i(TAG, String.format("creating FitnessService with key %s", key));
        return blueprints.get(key).create(homeScreenActivity);
    }

    public interface BluePrint {
        FitnessService create(HomeScreenActivity homeScreenActivity);
    }
}
