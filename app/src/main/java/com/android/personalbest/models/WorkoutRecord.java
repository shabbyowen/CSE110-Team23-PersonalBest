package com.android.personalbest.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.android.personalbest.fitness.FitnessService;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class WorkoutRecord {

    private static final String TAG = "WorkoutRecord";
    private static final String SESSION_SHARED_PREF = "personal_best_workout_record";
    private static final String SESSION_LIST = "session_list";
    private static WorkoutRecord instance;

    private SharedPreferences sharedPreferences;
    private FitnessService fitness;
    private List<Session> sessions;
    private Session currentSession;
    private AsyncTask task;

    public static WorkoutRecord getInstance(Context context, FitnessService fitness) {
        if (instance == null) {
            instance = new WorkoutRecord(context, fitness);
        }
        return instance;
    }

    public class Session {

        private LocalDateTime startTime;
        private int startStep;

        public Session(LocalDateTime startTime, int startStep) {
            this.startTime = startTime;
            this.startStep = startStep;
        }

        public LocalDateTime getStartTime() {
            return startTime;
        }

        public int getStartStep() {
            return startStep;
        }
    }

    interface UpdateListener{
        void onTimeElapsed(int value);
        void onStepWalked(int value);
    }

    public WorkoutRecord(Context context, FitnessService fitness) {
        sharedPreferences = context.getSharedPreferences(SESSION_SHARED_PREF, Context.MODE_PRIVATE);
        this.fitness = fitness;
        sessions = new LinkedList<>();
        currentSession = null;
        task = null;
    }

    public void startWorkout(LocalDateTime now, int startStep) {
        if (currentSession == null) {
            currentSession = new Session(now, startStep);
        } else {
            Log.e(TAG, "A session is already running!");
        }
    }

    private void startNotifyingListeners() {

    }
}
