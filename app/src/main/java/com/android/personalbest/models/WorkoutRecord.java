package com.android.personalbest.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.personalbest.HomeScreenActivity;
import com.android.personalbest.fitness.FitnessService;
import com.android.personalbest.util.DateCalculator;
import com.android.personalbest.util.TimeMachine;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class WorkoutRecord extends Model implements Model.Listener {

    public static final String TAG = "WorkoutRecord";
    public static final String SESSION_SHARED_PREF = "personal_best_workout_record";
    public static final String SESSION_LIST = "session_list";
    public static final String SESSION_SAVE_TIME = "session_saved_time";
    public static final String RUNNING_SESSION = "running_session";

    private static WorkoutRecord instance;

    private SharedPreferences sharedPreferences;
    private FitnessService fitnessService;
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
            currentSession = new Session(now, startStep);
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

    public void setFitnessService(FitnessService fitnessService) {
        this.fitnessService = fitnessService;
    }

    public void setTime(long time) {
        if (currentSession != null) {
            currentSession.deltaTime = time - currentSession.startTime;
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

        // save sessions
        Gson gson = new Gson();
        String data = gson.toJson(sessions);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor
            .putString(SESSION_LIST, data)
            .putLong(SESSION_SAVE_TIME, TimeMachine.nowMillis());
        Log.d(TAG, "Sessions saved");

        // save running session
        if (currentSession != null) {
            Log.d(TAG, "Saving running session");
            editor.putString(RUNNING_SESSION, gson.toJson(currentSession));
        } else {
            Log.d(TAG, "No running session");
            editor.putString(RUNNING_SESSION, "");
        }

        editor.apply();
    }

    public void load() {

        // load all the sessions
        Gson gson = new Gson();
        String data = sharedPreferences.getString(SESSION_LIST, "");
        Type type = new TypeToken<List<Session>>() {}.getType(); // REFLECTION BLACK MAGIC!
        sessions = gson.fromJson(data, type);
        if (sessions == null) {
            Log.d(TAG, "No sessions found, creating a new profile");
            sessions = new LinkedList<>();
        }
        Log.d(TAG, "Sessions loaded");


        // check if we have saved any running session
        Session session = gson.fromJson(sharedPreferences.getString(RUNNING_SESSION, ""), Session.class);
        if (session != null) {
            Log.d(TAG, "Running session found");

            // check if it has been a day
            long startTime = session.startTime;
            long currentTime = TimeMachine.nowMillis();

            if (DateCalculator.dateChanged(startTime, currentTime)) {

                // get the step from fitness service
                fitnessService.updateStepCountWithCallback(new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        List<DataPoint> list = dataSet.getDataPoints();
                        int yesterdayStep = list.get(1).getValue(Field.FIELD_STEPS).asInt();
                        currentSession.deltaStep = yesterdayStep - currentSession.startStep;
                        currentSession.deltaTime = 86400L * 1000L - session.startTime;
                        sessions.add(currentSession);
                        currentSession = null;
                        Log.d(TAG, "Date change detected!");
                    }
                });
            } else {
                currentSession = session;
                Log.d(TAG, "No date change");
            }

        } else {
            Log.d(TAG, "No running session found");
            currentSession = null;
        }
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
