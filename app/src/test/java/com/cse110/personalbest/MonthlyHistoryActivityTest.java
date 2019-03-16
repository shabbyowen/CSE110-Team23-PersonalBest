package com.cse110.personalbest;

import android.content.Intent;
import android.view.KeyEvent;

import com.cse110.personalbest.Activities.ChatHistoryActivity;
import com.cse110.personalbest.Activities.MonthlyHistoryActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;


@RunWith(RobolectricTestRunner.class)
public class MonthlyHistoryActivityTest {
    private MonthlyHistoryActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.buildActivity(MonthlyHistoryActivity.class).create().get();
    }

    @Test
    public void testBackKey() {
        activity.onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        Assert.assertTrue(activity.isFinishing());
    }

    @Test
    public void testNavigateUp() {
        activity.onSupportNavigateUp();
        Assert.assertTrue(activity.isFinishing());
    }

    @Test
    public void testOpenChatActivity() {
        Intent expected = new Intent(activity, ChatHistoryActivity.class);
        activity.openChatActivity();
        ShadowActivity shadow = Shadows.shadowOf(activity);
        Intent actual = shadow.getNextStartedActivity();
        Assert.assertTrue(expected.filterEquals(actual));
    }
}
