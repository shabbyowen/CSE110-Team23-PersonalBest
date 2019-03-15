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
import com.cse110.personalbest.Services.FriendService;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SendFriendRequestUITest {

    @Rule
    public ActivityTestRule<HomeActivity> mActivityTestRule = new ActivityTestRule<HomeActivity>(HomeActivity.class, false, false);

    @Test
    public void inputDiagramFragmentTest() {
        Intent intent = new Intent();
        intent.putExtra(HomeActivity.STEP_SERVICE_KEY_EXTRA, StepServiceSelector.MOCK_STEP_SERVICE_KEY);
        intent.putExtra(HomeActivity.SESSION_SERVICE_KEY_EXTRA, SessionServiceSelector.MOCK_SESSION_SERVICE_KEY);
        intent.putExtra(HomeActivity.FRIEND_SERVICE_KEY_EXTRA, FriendServiceSelector.MOCK_FRIEND_SERVICE_KEY);
        intent.putExtra(HomeActivity.STORAGE_SOLUTION_KEY_EXTRA, StorageSolutionFactory.MOCK_DICT_KEY);
        mActivityTestRule.launchActivity(intent);

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_friend), withContentDescription("Friends"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.action_add_friend), withContentDescription("add_friend"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.app_toolbar),
                                        1),
                                1),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.fragment_add_friend_input_dialog_tv), withText("Please enter the user's email address"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Please enter the user's email address")));

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.fragment_add_friend_input_dialog_et),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("555"), closeSoftKeyboard());

        ViewInteraction editText = onView(
                allOf(withId(R.id.fragment_add_friend_input_dialog_et), withText("555"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                1),
                        isDisplayed()));
        editText.check(matches(withText("555")));
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
