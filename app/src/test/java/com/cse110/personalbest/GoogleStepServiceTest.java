package com.cse110.personalbest;

import android.app.Activity;
import android.content.Intent;

import com.cse110.personalbest.Activities.HomeActivity;
import com.cse110.personalbest.Services.GoogleStepService;
import com.cse110.personalbest.Services.StepService;
import com.cse110.personalbest.Events.StepServiceCallback;
import com.cse110.personalbest.Factories.StorageSolutionFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class GoogleStepServiceTest {

    private StepService service;

    @Before
    public void setup() {
        Activity activity = Robolectric.buildActivity(HomeActivity.class).create().get();
        Intent intent = new Intent(activity, GoogleStepService.class);
        intent.putExtra(GoogleStepService.STORAGE_SOLUTION_KEY_EXTRA, StorageSolutionFactory.MOCK_DICT_KEY);
        service = new GoogleStepService();
        ((GoogleStepService) service).onStartCommand(intent, 0, 0);
    }

    @Test
    public void testGetWeekGoal() {
        service.getWeekGoal(new StepServiceCallback() {
            @Override
            public void onGoalResult(List<Integer> result) {
                List<Integer> expected = Arrays.asList(5000, 5000, 5000, 5000, 5000, 123, 321);
                Assert.assertEquals(expected, result);
            }
        });
    }

    @Test
    public void testGetTodayGoal() {
        service.getTodayGoal(new StepServiceCallback() {
            @Override
            public void onGoalResult(List<Integer> result) {
                List<Integer> expected = Arrays.asList(321);
                Assert.assertEquals(expected, result);
            }
        });
    }
}
