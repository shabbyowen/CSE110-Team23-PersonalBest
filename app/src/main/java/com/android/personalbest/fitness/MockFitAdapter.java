package com.android.personalbest.fitness;

import android.app.Activity;
import com.android.personalbest.HomeScreenActivity;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;

public class MockFitAdapter implements FitnessService {

    private HomeScreenActivity activity;

    public MockFitAdapter(HomeScreenActivity activity) {
        this.activity = activity;
    }

    @Override
    public int getRequestCode() {
        return 0;
    }

    @Override
    public void setup() {
    }

    @Override
    public void addStepCount(DataSet dataset) {
        // do nothing
    }

    @Override
    public void updateStepCount() {
        activity.setStepCount(1000);
    }

    @Override
    public void updateStepCountWithCallback(OnSuccessListener<DataReadResponse> successListener) {}
}
