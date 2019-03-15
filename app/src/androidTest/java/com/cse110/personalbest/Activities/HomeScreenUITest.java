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
import com.cse110.personalbest.Utilities.StorageSolution;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class HomeScreenUITest {

    @Rule
    public ActivityTestRule<HomeActivity> mActivityTestRule = new ActivityTestRule<HomeActivity>(HomeActivity.class, false, false);

    @Test
    public void homeScreenUITest() {
        Intent intent = new Intent();
        intent.putExtra(HomeActivity.STEP_SERVICE_KEY_EXTRA, StepServiceSelector.MOCK_STEP_SERVICE_KEY);
        intent.putExtra(HomeActivity.SESSION_SERVICE_KEY_EXTRA, SessionServiceSelector.MOCK_SESSION_SERVICE_KEY);
        intent.putExtra(HomeActivity.FRIEND_SERVICE_KEY_EXTRA, FriendServiceSelector.MOCK_FRIEND_SERVICE_KEY);
        intent.putExtra(HomeActivity.STORAGE_SOLUTION_KEY_EXTRA, StorageSolutionFactory.MOCK_DICT_KEY);
        mActivityTestRule.launchActivity(intent);


        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        ViewInteraction button = onView(
                allOf(withId(R.id.daily_goal_record_btn),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        2),
                                0),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withId(R.id.daily_goal_current_session_tv), withText("Current Session"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Current Session")));

        ViewInteraction button2 = onView(
                allOf(withId(R.id.daily_goal_change_goal_btn),
                        childAtPosition(
                                allOf(withId(R.id.daily_goal_steps_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                0)),
                                3),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.daily_goal_current_time_tv), withText("--:--:--"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.daily_goal_current_session_info_layout),
                                        0),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText("--:--:--")));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.daily_goal_change_goal_btn), withText("Change Goal"),
                        childAtPosition(
                                allOf(withId(R.id.daily_goal_steps_layout),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatButton2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.fragment_goal_input_dialog_tv), withText("Please enter your new step goal."),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                0),
                        isDisplayed()));
        textView3.check(matches(withText("Please enter your new step goal.")));
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
