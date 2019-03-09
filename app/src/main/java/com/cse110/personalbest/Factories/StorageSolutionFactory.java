package com.cse110.personalbest.Factories;

import android.content.Context;

import com.cse110.personalbest.Utilities.MockStorageSolution;
import com.cse110.personalbest.Utilities.SharedPrefStorageSolution;
import com.cse110.personalbest.Utilities.StorageSolution;

// TODO: change the creation method to instance method and add a super class
public class StorageSolutionFactory {

    public static final String SHARED_PREF_KEY = "shared_pref_storage_solution";
    public static final String MOCK_DICT_KEY = "mocked_dict_storage_solution";

    public static StorageSolution create(String key, Context context) {
        switch (key) {
            case SHARED_PREF_KEY:
                return new SharedPrefStorageSolution(context);
            default:
                return create(key);
        }
    }

    public static StorageSolution create(String key) {
        switch (key) {
            case MOCK_DICT_KEY:
                return new MockStorageSolution(); // to be created
            default:
                return null;
        }
    }
}
