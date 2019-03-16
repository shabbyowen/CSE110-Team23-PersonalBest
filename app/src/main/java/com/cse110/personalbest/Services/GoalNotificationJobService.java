package com.cse110.personalbest.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.cse110.personalbest.Activities.HomeActivity;
import com.cse110.personalbest.Events.MyBinder;
import com.cse110.personalbest.Events.StepServiceCallback;
import com.cse110.personalbest.Factories.ServiceSelector;
import com.cse110.personalbest.Factories.StepServiceSelector;
import com.cse110.personalbest.Factories.StorageSolutionFactory;
import com.cse110.personalbest.R;
import com.cse110.personalbest.Utilities.DateCalculator;
import com.cse110.personalbest.Utilities.StorageSolution;
import com.cse110.personalbest.Utilities.TimeMachine;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.cse110.personalbest.Services.GoogleStepService.DAILY_GOAL;

public class GoalNotificationJobService extends JobService {

    private static final String TAG = GoalNotificationJobService.class.getSimpleName();
    private StorageSolution storageSolution;
    private String storageSolutionKey = StorageSolutionFactory.SHARED_PREF_KEY;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob: job started!");
        storageSolution = StorageSolutionFactory.create(storageSolutionKey, this);
        getTodayStep(new StepServiceCallback(){
            @Override
            public void onStepResult(List<Integer> result) {
                final int step = result.get(0);
                getTodayGoal(new StepServiceCallback() {
                    @Override
                    public void onGoalResult(List<Integer> result) {
                        int goal = result.get(0);

                        Date lastGoalMetDate = new Date(storageSolution.get(GoogleStepService.LAST_GOAL_MET, 0L));
                        Date now = TimeMachine.now();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(now);

                        if (!DateCalculator.isSameDate(lastGoalMetDate, now) && step >= goal) {
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(GoalNotificationJobService.this, HomeActivity.CHANNEL_ID)
                                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                                .setContentTitle("Personal Best")
                                .setContentText("Congrats you reached your goal!")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setAutoCancel(true);
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(GoalNotificationJobService.this);
                            notificationManager.notify(0x12345678, builder.build());
                        }
                    }
                });
            }
        });
        return true;
    }

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

    public void getTodayGoal(StepServiceCallback callback) {
        List<GoogleStepService.GoalInfo> list = loadGoalInfo();
        List<Integer> result = new LinkedList<>();
        int goal = 5000;
        if (list != null && !list.isEmpty()) {
            goal = list.get(list.size() - 1).goal;
        }
        result.add(goal);
        callback.onGoalResult(result);
    }

    public List<GoogleStepService.GoalInfo> loadGoalInfo() {
        Gson gson = new Gson();
        Type type = new TypeToken<List<GoogleStepService.GoalInfo>>() {}.getType();
        String json = storageSolution.get(DAILY_GOAL, "");
        return gson.fromJson(json, type);
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
