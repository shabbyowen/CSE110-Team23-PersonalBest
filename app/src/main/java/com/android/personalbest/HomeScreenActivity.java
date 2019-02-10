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

public class HomeScreenActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Fragment currentFragment;
    private Fragment dailyGoalFragment;
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
        putFragment(new DailyGoalFragment());

        // set up bottom navigation menu
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
}
