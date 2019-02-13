package com.android.personalbest.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class WorkoutRecord implements StepCounter.Listener{

    private static final String TAG = "WorkoutRecord";
    private static final String SESSION_SHARED_PREF = "personal_best_workout_record";
    private static final String SESSION_LIST = "session_list";
    private static final int UPDATE_SEC = 5;
    private static WorkoutRecord instance;

    private SharedPreferences sharedPreferences;
    private List<Session> sessions;
    private Session currentSession;
    private List<Listener> listeners;

    public static WorkoutRecord getInstance(Context context) {
        if (instance == null) {
            instance = new WorkoutRecord(context);
        }
        return instance;
    }

    public class Session {

        private long startTime;
        private int startStep;

        public Session(long startTime, int startStep) {
            this.startTime = startTime;
            this.startStep = startStep;
        }

        public long getStartTime() {
            return startTime;
        }

        public int getStartStep() {
            return startStep;
        }
    }

    public interface Listener {
        void onSecondElapsed(int value);
        void onStepWalked(int value);
    }

    public WorkoutRecord(Context context) {
        sharedPreferences = context.getSharedPreferences(SESSION_SHARED_PREF, Context.MODE_PRIVATE);
        sessions = new LinkedList<>();
        currentSession = null;
        listeners = new LinkedList<>();
    }

    public void startWorkout(long now, int startStep) {
        if (currentSession == null) {
            currentSession = new Session(now, startStep);
        } else {
            Log.e(TAG, "A session is already running!");
        }
    }

    public void endWorkout() {
        if (currentSession != null) {
            currentSession = null;
        } else {
            Log.e(TAG, "No session is currently running!");
        }
    }

    public boolean isWorkingout() {
        return currentSession != null;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void updateTime(long time) {
        if (currentSession != null) {
            long deltaTime = (time - currentSession.getStartTime()) / 1000L;
            for (Listener listener: listeners) {
                listener.onSecondElapsed((int)deltaTime);
            }
        }
    }

    @Override
    public void onStepChanged(int value) {
        if (currentSession != null) {
            int deltaStep = value - currentSession.getStartStep();
            for (Listener listener: listeners) {
                listener.onStepWalked(deltaStep);
            }
        }
    }

    @Override
    public void onGoalChanged(int value) {
        // do nothing
    }
}
