package com.cse110.personalbest;

import android.view.KeyEvent;

import com.cse110.personalbest.Activities.ChatHistoryActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class ChatHistoryActivityTest {
    private ChatHistoryActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.buildActivity(ChatHistoryActivity.class).create().get();
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
}
