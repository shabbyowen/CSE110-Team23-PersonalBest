package com.cse110.personalbest.Services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.cse110.personalbest.Events.MyBinder;
import com.cse110.personalbest.Events.ObservableServiceListener;
import com.cse110.personalbest.Events.StepServiceCallback;
import com.cse110.personalbest.Events.StepServiceListener;
import com.cse110.personalbest.Utilities.DateCalculator;
import com.cse110.personalbest.Factories.StorageSolutionFactory;
import com.cse110.personalbest.Utilities.StorageSolution;
import com.cse110.personalbest.Utilities.TimeMachine;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataUpdateRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class GoogleStepService extends StepService {

    public static final String DAILY_GOAL = "daily_goal";
    public static final String LAST_GOAL_MET = "last_goal_met";
    public static final String LAST_ENCOURAGEMENT = "last_encouragement";
    private static final String TAG = "GoogleStepService";
    private static final int UPDATE_DELAY = 10 * 1000;

    private final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = System.identityHashCode(this) & 0xFFFF;
    private final IBinder binder = new GoogleStepServiceBinder();

    private String storageSolutionKey = StorageSolutionFactory.SHARED_PREF_KEY;
    private Handler handler = new Handler();
    private StorageSolution storageSolution;

    // binder for activity to communicate with this service
    public class GoogleStepServiceBinder extends MyBinder {

        @Override
        public Service getService() {
            return GoogleStepService.this;
        }
    }

    private Runnable stepUpdateTask = new Runnable() {
        @Override
        public void run() {
            notifyListener();
            handler.postDelayed(stepUpdateTask, UPDATE_DELAY);
        }
    };

    private class GoalInfo {
        public int goal;
        public long time;

        public GoalInfo(int goal, long time) {
            this.goal = goal;
            this.time = time;
        }
    }

    public GoogleStepService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            // use the correct storage solution base on key
            String key = intent.getStringExtra(STORAGE_SOLUTION_KEY_EXTRA);
            if (key != null) {
                storageSolutionKey = key;
            }
        } else {
            storageSolutionKey = StorageSolutionFactory.SHARED_PREF_KEY;
        }

        // TODO: figure out what will happen when onStartCommand is called twice
        storageSolution = StorageSolutionFactory.create(storageSolutionKey, this);

        // make sure this service runs throughout the entire app life cycle
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void setup(Activity activity) {

        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this);

        FitnessOptions fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
            .build();

        if (!GoogleSignIn.hasPermissions(lastSignedInAccount, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                activity, // your activity
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                lastSignedInAccount,
                fitnessOptions);
        } else {
            startRecording();
        }

        // make sure that we don't have multiple update task running
        handler.removeCallbacks(stepUpdateTask);
        handler.post(stepUpdateTask);
    }

    @Override
    public void getTodayStep(final StepServiceCallback callback) {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (lastSignedInAccount == null) {
            Log.w(TAG, "No account signed in!");
            return;
        }

        Log.d(TAG, "Trying to get today step count...");
        Fitness.getHistoryClient(this, lastSignedInAccount)
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener(
                new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        Log.d(TAG, dataSet.toString());
                        long total =
                            dataSet.isEmpty()
                                ? 0
                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                        List<Integer> result = new LinkedList<>();
                        result.add((int)total);
                        callback.onStepResult(result);
                    }
                })
            .addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "There was a problem getting the step count.", e);
                    }
                });
    }

    @Override
    public void addStep(int step) {

        // calculate time
        Calendar cal = Calendar.getInstance();
        Date now = TimeMachine.now();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MINUTE, -1);
        long startTime = cal.getTimeInMillis();

        // Create a data source
        DataSource dataSource =
            new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setStreamName("step count")
                .setType(DataSource.TYPE_RAW)
                .build();

        // Create a data set
        DataSet dataSet = DataSet.create(dataSource);
        DataPoint dataPoint = dataSet.createDataPoint().setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_STEPS).setInt(step);
        dataSet.add(dataPoint);

        // build the request
        DataUpdateRequest request =
            new DataUpdateRequest.Builder()
                .setDataSet(dataSet)
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        // Invoke the History API to update data.
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
            .updateData(request)
            .addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        // At this point the data has been updated and can be read.
                        Log.i(TAG, "Data update was successful.");
                    } else {
                        Log.e(TAG, "There was a problem updating the dataset.", task.getException());
                    }
                });
    }

    @Override
    public void getStep(int day, final StepServiceCallback callback) {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (lastSignedInAccount == null) {
            Log.w(TAG, "No account signed in!");
            return;
        }

        Log.d(TAG, "Trying to get this week step count...");
        Fitness.getHistoryClient(this, lastSignedInAccount)
            .readData(readRequest(day))
            .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                @Override
                public void onSuccess(DataReadResponse dataReadResponse) {
                    List<Bucket> buckets = dataReadResponse.getBuckets();
                    List<Integer> result = new LinkedList<>();
                    Log.d(TAG, buckets.toString());
                    for (Bucket bucket : buckets) {
                        DataSet dataSet = bucket.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA);
                        if (dataSet == null || dataSet.isEmpty()) {
                            result.add(0);
                        } else {
                            result.add(dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt());
                        }
                    }
                    callback.onStepResult(result);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "There was a problem getting the step count.", e);
                }
            });
    }

    @Override
    public void setGoal(int goal) {
        Log.d(TAG, "Setting goal: " + goal);
        List<GoalInfo> list = loadGoalInfo();
        if (list == null || list.isEmpty()) {
            list = new LinkedList<>();
            list.add(new GoalInfo(goal, TimeMachine.nowMillis()));
        } else {
            GoalInfo newest = list.get(list.size() - 1);
            int diff = DateCalculator.dateDifference(TimeMachine.now(), new Date(newest.time));
            if (diff < 0) {

                // no no no no, this app is not stable enough for time travellers
                Log.w(TAG, "Warning, space-time anomaly detected when setting the goal. Prepare for crashes");
            } else if (diff == 0) {

                // update today's goal
                newest.goal = goal;
            } else {

                // insert goals for days before
                for (int i = 0; i < diff - 1; i++) {
                    list.add(new GoalInfo(newest.goal, newest.time + (i + 1) * (86400 * 1000)));
                }
                list.add(new GoalInfo(goal, TimeMachine.nowMillis()));
            }
        }

        // save the result
        saveGoalInfo(list);

        // notify goal change
        notifyListener();
    }

    @Override
    public void getTodayGoal(StepServiceCallback callback) {
        List<GoalInfo> list = loadGoalInfo();
        List<Integer> result = new LinkedList<>();
        int goal = 5000;
        if (list != null && !list.isEmpty()) {
            goal = list.get(list.size() - 1).goal;
        }
        result.add(goal);
        callback.onGoalResult(result);
    }

    // TODO: week goal is not displayed properly
    @Override
    public void getGoal(int day, StepServiceCallback callback) {
        List<GoalInfo> list = loadGoalInfo();
        List<Integer> result = new LinkedList<>();

        if (list == null) {
            list = new LinkedList<>();
        }

        if (list.size() > day) {
            list = list.subList(list.size() - day, list.size());
        }
        if (list.size() < day) {
            while (list.size() < day) {
                list.add(0, new GoalInfo(5000, 0));
            }
        }
        for (GoalInfo info : list) {
            result.add(info.goal);
        }
        callback.onGoalResult(result);
    }

    @Override
    public void notifyListener() {
        // TODO: break this up into smaller methods
        getTodayStep(new StepServiceCallback() {
            @Override
            public void onStepResult(List<Integer> result) {

                final int step = result.get(0);

                getTodayGoal(new StepServiceCallback() {
                    @Override
                    public void onGoalResult(List<Integer> result) {

                        final int goal = result.get(0);

                        // check if should display encouragement
                        getStep(2, new StepServiceCallback() {
                            @Override
                            public void onStepResult(List<Integer> result) {

                                int yesterdayStep = result.get(result.size() - 2);
                                Date lastEncouragementDate = new Date(storageSolution.get(LAST_ENCOURAGEMENT, (long) 0));
                                Date lastGoalMetDate = new Date(storageSolution.get(LAST_GOAL_MET, 0L));
                                Date now = TimeMachine.now();
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(now);

                                boolean shouldPromptGoal =
                                    !DateCalculator.isSameDate(lastGoalMetDate, now) &&
                                    step >= goal;

                                boolean shouldPromptEncouragement =
                                    !DateCalculator.isSameDate(lastEncouragementDate, now) &&
                                    cal.get(Calendar.HOUR_OF_DAY) > 20 &&
                                    (step - yesterdayStep) / 500 > 0;

                                // notify the listeners
                                for (ObservableServiceListener listener : listeners) {
                                    StepServiceListener stepListener = (StepServiceListener) listener;
                                    stepListener.onStepChanged(step);
                                    stepListener.onGoalChanged(goal);
                                    if (shouldPromptGoal) {
                                        stepListener.onGoalMet();
                                    } else if (shouldPromptEncouragement) {
                                        stepListener.onEncouragement();
                                    }
                                }

                                // update prompt display time
                                if (shouldPromptGoal) {
                                    storageSolution.put(LAST_GOAL_MET, TimeMachine.nowMillis());
                                } else if (shouldPromptEncouragement) {
                                    storageSolution.put(LAST_ENCOURAGEMENT, TimeMachine.nowMillis());
                                }
                            }
                        });
                    }
                });
            }
        });

    }

    public List<GoalInfo> loadGoalInfo() {
        Gson gson = new Gson();
        Type type = new TypeToken<List<GoalInfo>>() {}.getType();
        String json = storageSolution.get(DAILY_GOAL, "");
        return gson.fromJson(json, type);
    }

    public void saveGoalInfo(List<GoalInfo> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        storageSolution.put(DAILY_GOAL, json);
    }

    public void startRecording() {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (lastSignedInAccount == null) {
            Log.w(TAG, "No account signed in!");
            return;
        }

        Fitness.getRecordingClient(this, lastSignedInAccount)
            .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i(TAG, "Successfully subscribed!");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG, "There was a problem subscribing.");
                }
            });
    }

    public DataReadRequest readRequest(int day) {

        // get today midnight
        Date end = DateCalculator.toClosestMidnightTmr(TimeMachine.now());
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        cal.add(Calendar.DATE, -day);
        Date start = cal.getTime();

        // create request
        return new DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .setTimeRange(start.getTime(), end.getTime(), TimeUnit.MILLISECONDS)
            .bucketByTime(1, TimeUnit.DAYS)
            .build();
    }
}
