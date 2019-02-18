package com.android.personalbest;


import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class DailyGoalFragmentUnitTest {
    HomeScreenActivity activity;
    WeeklyProgressFragment myFragment;

    @Before
    public void init() {
        /* activity = Robolectric.setupActivity(HomeScreenActivity.class);
        myFragment = new WeeklyProgressFragment();
        startFragment(myFragment); */
    }

    @Test
    public void isNotNull() {
        //assertNotNull(myFragment);
    }
}
