package com.cse110.personalbest.Services;

import com.cse110.personalbest.Events.SessionServiceCallback;

public abstract class SessionService extends ObservableService {

    public static final String STORAGE_SOLUTION_KEY_EXTRA = "storage_solution_key_extra";
    public static final String STEP_SERVICE_KEY_EXTRA = "step_service_key_extra";

    public abstract void startSession();
    public abstract void endSession();
    public abstract void saveNow();
    public abstract boolean isWorkingOut();
    public abstract void getSession(int day, SessionServiceCallback callback);
    public abstract void uploadMonthlyProgress();
}
