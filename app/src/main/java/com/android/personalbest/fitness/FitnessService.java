package com.android.personalbest.fitness;

import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.tasks.OnSuccessListener;

public interface FitnessService {
    int getRequestCode();
    void setup();
    void updateStepCount();
    void updateStepCountWithCallback(OnSuccessListener<DataSet> successListener);
}
