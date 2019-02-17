package com.android.personalbest.fitness;

import android.service.autofill.Dataset;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.personalbest.util.DateCalculator;
import com.android.personalbest.util.TimeMachine;
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
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.android.personalbest.HomeScreenActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.tasks.Tasks.await;

public class GoogleFitAdapter implements FitnessService {
    private final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = System.identityHashCode(this) & 0xFFFF;
    private final String TAG = "GoogleFitAdapter";

    private HomeScreenActivity activity;

    public GoogleFitAdapter(HomeScreenActivity activity) {
        this.activity = activity;
    }


    public void setup() {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(activity), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    activity, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(activity),
                    fitnessOptions);
        } else {
            updateStepCount();
            startRecording();
        }
    }


    private void startRecording() {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (lastSignedInAccount == null) {
            return;
        }

        Fitness.getRecordingClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
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


    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    public void updateStepCount() {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (lastSignedInAccount == null) {
            Log.w(TAG, "No account signed in!");
            return;
        }

        Fitness.getHistoryClient(activity, lastSignedInAccount)
            .readData(readRequest())
            .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                @Override
                public void onSuccess(DataReadResponse dataReadResponse) {
                    List<Bucket> buckets = dataReadResponse.getBuckets();
                    DataSet dataSet = buckets.get(buckets.size() - 1).getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA);
                    int total = dataSet == null || dataSet.isEmpty() ? 0 :
                        dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                    activity.setStepCount(total);
                    dataSet = buckets.get(buckets.size() - 2).getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA);
                    int yesterdayTotal = dataSet == null || dataSet.isEmpty() ? 0 :
                        dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                    activity.setYesterdayStepCount(yesterdayTotal);
                    Log.d(TAG, "today total " + total);
                    Log.d(TAG, "yesterday total " + yesterdayTotal);
                    Log.d(TAG, "buckets " + buckets.toString());
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
    public void updateStepCountWithCallback(OnSuccessListener<DataReadResponse> successListener) {

        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (lastSignedInAccount == null) {
            Log.w(TAG, "No account signed in!");
            return;
        }

        Fitness.getHistoryClient(activity, lastSignedInAccount)
            .readData(readRequest())
            .addOnSuccessListener(successListener)
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "There was a problem getting the step count.", e);
                }
            });
    }

    private DataReadRequest readRequest() {

        // get today midnight
        Calendar cal = DateCalculator.toClosesetMinightTmr(TimeMachine.nowCal());

        // calculate time frame
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        // create request
        return new DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .bucketByTime(1, TimeUnit.DAYS)
            .build();
    }

    @Override
    public int getRequestCode() {
        return GOOGLE_FIT_PERMISSIONS_REQUEST_CODE;
    }

    @Override
    public void addStepCount(DataSet dataSet) {

        // calculate the time frame
        long endTime = dataSet.getDataPoints().get(0).getEndTime(TimeUnit.MILLISECONDS);
        long startTime = dataSet.getDataPoints().get(0).getStartTime(TimeUnit.MILLISECONDS);

        // build the request
        DataUpdateRequest request =
            new DataUpdateRequest.Builder()
                .setDataSet(dataSet)
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        // Invoke the History API to update data.
        Fitness.getHistoryClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
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
}
