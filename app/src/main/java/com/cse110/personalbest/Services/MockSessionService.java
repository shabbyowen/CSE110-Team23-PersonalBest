package com.cse110.personalbest.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.cse110.personalbest.Events.MyBinder;
import com.cse110.personalbest.Events.SessionServiceCallback;

public class MockSessionService extends SessionService {

    private final IBinder binder = new MockSessionServiceBinder();

    public class MockSessionServiceBinder extends MyBinder {
        @Override
        public Service getService() {
            return MockSessionService.this;
        }
    }

    @Override
    public void startSession() {

    }

    @Override
    public void endSession() {

    }

    @Override
    public void saveNow() {

    }

    @Override
    public boolean isWorkingOut() {
        return false;
    }

    @Override
    public void getWeekSession(SessionServiceCallback callback) {

    }

    @Override
    public void notifyListener() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
