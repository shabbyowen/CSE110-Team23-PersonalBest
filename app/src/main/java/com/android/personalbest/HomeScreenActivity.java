package com.android.personalbest;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.personalbest.fitness.FitnessService;
import com.android.personalbest.fitness.FitnessServiceFactory;
import com.android.personalbest.fitness.GoogleFitAdapter;


public class HomeScreenActivity extends AppCompatActivity implements HeightPromptFragment.HeightPromptListener {
    private static final String FITNESS_API_KEY = "HOME_SCREEN_KEY";
    private static final String INPUT_HEIGHT = "INPUT_HEIGHT";

    private FitnessService fitnessService;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Fragment currentFragment;
    private DailyGoalFragment dailyGoalFragment;
    private Fragment weeklyProgressFragment;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // init fragment manager
        fragmentManager = getSupportFragmentManager();

        // set up bottom navigation menu
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        FitnessServiceFactory.put(FITNESS_API_KEY, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(HomeScreenActivity stepCountActivity) {
                return new GoogleFitAdapter(stepCountActivity);
            }
        });
        fitnessService = FitnessServiceFactory.create(FITNESS_API_KEY, this);
        fitnessService.setup();

        HeightPromptFragment heightPromptFragment = HeightPromptFragment.newInstance(this, INPUT_HEIGHT, R.string.prompt_height_str);
        heightPromptFragment.show(fragmentManager, INPUT_HEIGHT);
        putFragment(new DailyGoalFragment());
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
        if (dailyGoalFragment == null) return;
        dailyGoalFragment.setStepCount(stepCount);
    }

    @Override
    public boolean onInputResult(String tag, String result, TextView view) {
        return true;
    }
}
