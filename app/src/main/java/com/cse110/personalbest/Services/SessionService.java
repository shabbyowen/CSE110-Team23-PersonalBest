package com.cse110.personalbest.Services;

import com.cse110.personalbest.Events.SessionServiceCallback;

public abstract class SessionService extends ObservableService {

    public static final String STORAGE_SOLUTION_KEY_EXTRA = "storage_solution_key_extra";

    public abstract void startSession();
    public abstract void endSession();
    public abstract void saveNow();
    public abstract boolean isWorkingOut();
    public abstract void getWeekSession(SessionServiceCallback callback);
}
