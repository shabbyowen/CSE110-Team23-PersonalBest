package com.android.personalbest.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class WorkoutRecord extends Model implements Model.Listener {

    private static final String TAG = "WorkoutRecord";
    private static final String SESSION_SHARED_PREF = "personal_best_workout_record";
    private static final String SESSION_LIST = "session_list";
    private static final int UPDATE_SEC = 5;
    private static WorkoutRecord instance;

    private SharedPreferences sharedPreferences;
    private List<Session> sessions;
    private Session currentSession;

    public static WorkoutRecord getInstance(Context context) {
        if (instance == null) {
            instance = new WorkoutRecord(context);
        }
        return instance;
    }

    public class Session {

        public long startTime;
        public int startStep;
        public long deltaTime;
        public int deltaStep;

        public Session(long startTime, int startStep) {
            this.startTime = startTime;
            this.startStep = startStep;
            this.deltaTime = 0;
            this.deltaStep = 0;
        }
    }

    public class Result {

        public int deltaTime;
        public int deltaStep;

        public Result(int deltaTime, int deltaStep) {
            this.deltaTime = deltaTime;
            this.deltaStep = deltaStep;
        }
    }

    public WorkoutRecord(Context context) {
        super();
        sharedPreferences = context.getSharedPreferences(SESSION_SHARED_PREF, Context.MODE_PRIVATE);
        sessions = new LinkedList<>();
        currentSession = null;
    }

    public void startWorkout(long now, int startStep) {
        if (currentSession == null) {
            currentSession = new Session(now / 1000L, startStep);
        } else {
            Log.e(TAG, "A session is already running!");
        }
    }

    public void endWorkout() {
        if (currentSession != null) {

            // record this session
            sessions.add(currentSession);
            currentSession = null;
        } else {
            Log.e(TAG, "No session is currently running!");
        }
    }

    public boolean isWorkingout() {
        return currentSession != null;
    }

    public void setTime(long time) {
        if (currentSession != null) {
            currentSession.deltaTime = time / 1000L - currentSession.startTime;
            updateAll();
        }
    }

    public void setStep(int step) {
        if (currentSession != null) {
            currentSession.deltaStep = step - currentSession.startStep;
            updateAll();
        }
    }

    public void updateAll() {
        update(new Result((int)currentSession.deltaTime, currentSession.deltaStep));
    }

    @Override
    public void onUpdate(Object o) {
        if (o instanceof StepCounter.Result) {
            StepCounter.Result result = (StepCounter.Result) o;
            setStep(result.step);
        }
    }
}
