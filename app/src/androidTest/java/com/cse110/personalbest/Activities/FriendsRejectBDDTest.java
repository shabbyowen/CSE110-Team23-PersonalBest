package com.cse110.personalbest.Activities;


import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.cse110.personalbest.Factories.FriendServiceSelector;
import com.cse110.personalbest.Factories.SessionServiceSelector;
import com.cse110.personalbest.Factories.StepServiceSelector;
import com.cse110.personalbest.Factories.StorageSolutionFactory;
import com.cse110.personalbest.R;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class FriendsRejectBDDTest {

    @Rule
    public ActivityTestRule<HomeActivity> mActivityTestRule = new ActivityTestRule<HomeActivity>(HomeActivity.class, false, false);

    private Intent homeActivityIntent;

    @Before
    public void setUp() {
        homeActivityIntent = new Intent();
        homeActivityIntent.putExtra(HomeActivity.FRIEND_SERVICE_KEY_EXTRA, FriendServiceSelector.MOCK_FRIEND_SERVICE_KEY);
        homeActivityIntent.putExtra(HomeActivity.SESSION_SERVICE_KEY_EXTRA, SessionServiceSelector.MOCK_SESSION_SERVICE_KEY);
        homeActivityIntent.putExtra(HomeActivity.STEP_SERVICE_KEY_EXTRA, StepServiceSelector.MOCK_STEP_SERVICE_KEY);
        homeActivityIntent.putExtra(HomeActivity.STORAGE_SOLUTION_KEY_EXTRA, StorageSolutionFactory.MOCK_DICT_KEY);
    }

    @Test
    public void friendListActivityTest() {
        mActivityTestRule.launchActivity(homeActivityIntent);

        ViewInteraction bottomNavigationItemView = onView(
            allOf(withId(R.id.navigation_friend), withContentDescription("Friends"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.navigation),
                        0),
                    1),
                isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction textView = onView(
            allOf(withId(R.id.tv_friend_email), withText("yal272@ucsd.edu"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.lv_friends),
                        0),
                    0),
                isDisplayed()));
        textView.check(matches(withText("yal272@ucsd.edu")));

        ViewInteraction textView2 = onView(
            allOf(withId(R.id.tv_friend_email), withText("jit072@ucsd.edu"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.lv_friends),
                        1),
                    0),
                isDisplayed()));
        textView2.check(matches(withText("jit072@ucsd.edu")));
    }

    /*@Test
    public void testRejectFriend() {
        mActivityTestRule.launchActivity(homeActivityIntent);
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_friend), withContentDescription("Friends"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.tv_pending_email), withText("test1@ucsd.edu"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.lv_pending),
                                        0),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("test1@ucsd.edu")));

        ViewInteraction imageButton = onView(
                allOf(withId(R.id.btn_pending_ignore),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.lv_pending),
                                        0),
                                2),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.btn_pending_ignore),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.lv_pending),
                                        0),
                                2),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.tv_pending_email), withText("test2@ucsd.edu"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.lv_pending),
                                        0),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("test2@ucsd.edu")));
    }*/

    @After
    public void after() {
        mActivityTestRule.finishActivity();
    }

    private static Matcher<View> childAtPosition(
        final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                    && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
