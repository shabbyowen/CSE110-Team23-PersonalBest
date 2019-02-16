package com.android.personalbest.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.personalbest.util.TimeMachine;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class WorkoutRecord extends Model implements Model.Listener {

    public static final String TAG = "WorkoutRecord";
    public static final String SESSION_SHARED_PREF = "personal_best_workout_record";
    public static final String SESSION_LIST = "session_list";
    public static final String SESSION_SAVE_TIME = "session_saved_time";

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
        load();
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

            // save the session list
            save();

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

    public void save() {

        // save as json
        Gson gson = new Gson();
        String data = gson.toJson(sessions);
        sharedPreferences.edit()
            .putString(SESSION_LIST, data)
            .putLong(SESSION_SAVE_TIME, TimeMachine.nowMillis())
            .apply();
        Log.d(TAG, "Sessions saved");
        Log.v(TAG, data);
    }

    public void load() {

        // load json
        Gson gson = new Gson();
        String data = sharedPreferences.getString(SESSION_LIST, "");
        Type type = new TypeToken<List<Session>>() {}.getType(); // REFLECTION BLACK MAGIC!
        sessions = gson.fromJson(data, type);
        Log.d(TAG, "Sessions loaded");
        Log.v(TAG, data);
    }

    public List<Session> getSessions() {
        return sessions;
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
