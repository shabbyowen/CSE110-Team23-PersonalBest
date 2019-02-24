package com.cse110.personalbest.Events;

public interface StepServiceListener extends ObservableServiceListener {

    void onStepChanged(int step);
    void onGoalChanged(int goal);
    void onGoalMet();
    void onEncouragement();
}
