package com.cse110.personalbest.Services;

import android.app.Activity;
import android.content.Intent;
import android.os.IBinder;

import com.cse110.personalbest.Events.StepServiceCallback;

public class MockStepService extends StepService {

    public MockStepService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void setup(Activity activity) {

    }

    @Override
    public void getTodayStep(StepServiceCallback callback) {
        
    }

    @Override
    public void getWeekStep(StepServiceCallback callback) {

    }

    @Override
    public void setGoal(int goal) {

    }

    @Override
    public void getTodayGoal(StepServiceCallback callback) {

    }

    @Override
    public void getWeekGoal(StepServiceCallback callback) {

    }

    @Override
    public void notifyListener() {

    }
}
