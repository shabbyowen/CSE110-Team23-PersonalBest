package com.android.personalbest;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import static org.junit.Assert.*;

import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

@RunWith(RobolectricTestRunner.class)
public class WeeklyProgressFragmentTest {
    HomeScreenActivity activity;
    WeeklyProgressFragment fragment;

    @Before
    public void init() {
        activity = Robolectric.setupActivity(HomeScreenActivity.class);
        fragment = new WeeklyProgressFragment();
        startFragment(fragment);
    }

    @Test
    public void checkNull() {
        assertNotNull(fragment);

    }
}
