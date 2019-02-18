package com.android.personalbest;

import android.util.Log;

import com.android.personalbest.fitness.FitnessService;
import com.android.personalbest.fitness.FitnessServiceFactory;
import com.android.personalbest.fitness.GoogleFitAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class GoogleFitAdapterTest {
    private static final String FITNESS_API_KEY = "HOME_SCREEN_KEY";
    FitnessService fitnessService;
    HomeScreenActivity activity;

    @Before
    public void init() {
        /*activity = Robolectric.setupActivity(HomeScreenActivity.class);
        fitnessService = FitnessServiceFactory.create(FITNESS_API_KEY, activity);*/
    }

    @Test
    public void testUpdateStepCountNoSignIn() {
        /* Test updating step count with no google account
        int beforeSteps = activity.getStepCount();
        fitnessService.updateStepCount();
        assertEquals(beforeSteps, activity.getStepCount());  */
    }
}
