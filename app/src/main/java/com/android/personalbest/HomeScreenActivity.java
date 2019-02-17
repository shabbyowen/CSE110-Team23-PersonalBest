package com.android.personalbest;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.personalbest.fitness.FitnessService;
import com.android.personalbest.fitness.FitnessServiceFactory;
import com.android.personalbest.fitness.GoogleFitAdapter;
import com.android.personalbest.models.EncouragementTracker;
import com.android.personalbest.models.StepCounter;
import com.android.personalbest.models.WorkoutRecord;
import com.android.personalbest.util.TimeMachine;

public class HomeScreenActivity extends AppCompatActivity implements HeightPromptFragment.HeightPromptListener{

    private static final String FITNESS_API_KEY = "HOME_SCREEN_KEY";
    private static final String INPUT_HEIGHT = "INPUT_HEIGHT";
    private static final String TAG = "HomeScreenActivity";
    private static final int UPDATE_DELAY_SEC = 5;

    private FitnessService fitnessService;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Fragment currentFragment;
    private DailyGoalFragment dailyGoalFragment;
    private Fragment weeklyProgressFragment;
    private Runnable updateStepTask;
    private Runnable updateTimeTask;
    private Handler handler;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
        = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    putFragment(
                        dailyGoalFragment == null ?
                        new DailyGoalFragment() :
                        dailyGoalFragment);
                    return true;
                case R.id.navigation_dashboard:
                    putFragment(
                        weeklyProgressFragment == null ?
                        new WeeklyProgressFragment() :
                        weeklyProgressFragment);
                    return true;

            }
            return false;
        }
    };

    // models
    private StepCounter counter;
    private WorkoutRecord record;
    private EncouragementTracker promptTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // get models
        counter = StepCounter.getInstance(this);
        record = WorkoutRecord.getInstance(this);
        record.setFitnessService(fitnessService);
        promptTracker = EncouragementTracker.getInstance(this);

        // init fragment manager
        fragmentManager = getSupportFragmentManager();

        // set up bottom navigation menu
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // fitness service connection
        FitnessServiceFactory.put(FITNESS_API_KEY, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(HomeScreenActivity stepCountActivity) {
                return new GoogleFitAdapter(stepCountActivity);
            }
        });
        fitnessService = FitnessServiceFactory.create(FITNESS_API_KEY, this);
        fitnessService.setup();

        // ask user for their height
        HeightPromptFragment heightPromptFragment = HeightPromptFragment.newInstance(this, INPUT_HEIGHT, R.string.prompt_height_str);
        heightPromptFragment.show(fragmentManager, INPUT_HEIGHT);
        putFragment(new DailyGoalFragment());

        // initialize update task
        handler = new Handler();
        updateStepTask = () -> {
            Log.d(TAG, "try to update the step count...");
            fitnessService.updateStepCount();
            handler.postDelayed(updateStepTask, UPDATE_DELAY_SEC * 1000);
        };
        updateTimeTask = () -> {
            record.setTime(TimeMachine.nowMillis());
            handler.postDelayed(updateTimeTask, 1000);
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        // the record model listens to the step update
        counter.addListener(record);

        // resume updating step count
        handler.post(updateStepTask);
        handler.post(updateTimeTask);
        Log.d(TAG, "Update tasks resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();

        // remove listeners
        counter.removeListener(record);

        // pause update task
        handler.removeCallbacks(updateStepTask);
        handler.removeCallbacks(updateTimeTask);
        Log.d(TAG, "Update tasks paused");
    }

    private void putFragment(Fragment fragment) {
        fragmentTransaction = fragmentManager.beginTransaction();

        // dont switch if the new fragment is the same
        if (currentFragment == fragment) {
            return;
        }

        // remove old fragment
        if (currentFragment != null) {
            fragmentTransaction.remove(currentFragment);
        }

        // add the new fragment and commit
        fragmentTransaction.add(R.id.home_screen_container, fragment);
        currentFragment = fragment;
        fragmentTransaction.commit();
    }

    public void setStepCount(long stepCount) {
        counter.setStep((int)stepCount);
    }

    @Override
    public boolean onInputResult(String tag, String result, TextView view) {
        return true;
    }
}
