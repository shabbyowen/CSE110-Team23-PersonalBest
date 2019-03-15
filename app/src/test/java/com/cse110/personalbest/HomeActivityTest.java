package com.cse110.personalbest;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.view.MenuItem;

import com.cse110.personalbest.Activities.HomeActivity;
import com.cse110.personalbest.Activities.MonthlyHistoryActivity;
import com.cse110.personalbest.Events.StepServiceCallback;
import com.cse110.personalbest.Factories.ServiceSelector;
import com.cse110.personalbest.Factories.StorageSolutionFactory;
import com.cse110.personalbest.Services.FriendService;
import com.cse110.personalbest.Services.GoogleStepService;
import com.cse110.personalbest.Services.MockFriendService;
import com.cse110.personalbest.Services.MockSessionService;
import com.cse110.personalbest.Services.MockStepService;
import com.cse110.personalbest.Services.StepService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowToast;

import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class HomeActivityTest {
    HomeActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.buildActivity(HomeActivity.class).create().get();
    }

    @Test
    public void testFriendRefreshButton() {
        MenuItem item = new RoboMenuItem(R.id.action_refresh_friends);
        activity.onOptionsItemSelected(item);
        Assert.assertEquals("Refreshing", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testFriendItemClickedStartsMonthlyHistoryActivity() {
        Intent expected = new Intent(activity, MonthlyHistoryActivity.class);
        activity.onFriendItemClicked(new Friend("test@test.com"));
        ShadowActivity shadow = Shadows.shadowOf(activity);
        Intent actual = shadow.getNextStartedActivity();
        Assert.assertTrue(expected.filterEquals(actual));
    }
}
