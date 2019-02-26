package com.cse110.personalbest.Services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.util.Log;
import com.cse110.personalbest.Events.MyBinder;
import com.cse110.personalbest.Events.StepServiceCallback;

public class MockStepService extends StepService {

    private static final String TAG = "MockStepService";

    private final IBinder binder = new MockSessionServiceBinder();

    public class MockSessionServiceBinder extends MyBinder {
        @Override
        public Service getService() {
            return MockStepService.this;
        }
    }

    public MockStepService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        return START_STICKY;
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
