package com.android.personalbest.fitness;


import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;

public interface FitnessService {
    int getRequestCode();
    void setup();
    void addStepCount(DataSet dataset);
    void updateStepCount();
    void updateStepCountWithCallback(OnSuccessListener<DataReadResponse> successListener);
}
