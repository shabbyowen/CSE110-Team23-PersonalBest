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
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ChatHistoryUITest {
    private static final String CHAT_FRIEND_EMAIL = "chat_friend_email";
    private static final String MY_EMAIL = "my_email";
    @Rule
    public ActivityTestRule<ChatHistoryActivity> mActivityTestRule = new ActivityTestRule<>(ChatHistoryActivity.class, false, false);

    @Test
    public void chatHistoryUITest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Intent intent = new Intent();
        intent.putExtra(HomeActivity.FRIEND_SERVICE_KEY_EXTRA, FriendServiceSelector.MOCK_FRIEND_SERVICE_KEY);
        intent.putExtra(CHAT_FRIEND_EMAIL, "yal272@ucsd.edu");
        intent.putExtra(MY_EMAIL, "sol014@ucsd.edu");
        mActivityTestRule.launchActivity(intent);

        ViewInteraction textView = onView(
            allOf(withId(R.id.tv_chat_history_toolbar_title), withText("yal272@ucsd.edu"),
                childAtPosition(
                    allOf(withId(R.id.app_toolbar),
                        childAtPosition(
                            IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
                            0)),
                    0),
                isDisplayed()));
        textView.check(matches(withText("yal272@ucsd.edu")));

        ViewInteraction editText = onView(
            allOf(withId(R.id.et_send_message),
                childAtPosition(
                    allOf(withId(R.id.layout_send_message),
                        childAtPosition(
                            IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
                            2)),
                    0),
                isDisplayed()));
        editText.check(matches(isDisplayed()));

        ViewInteraction button = onView(
            allOf(withId(R.id.btn_send_message),
                childAtPosition(
                    allOf(withId(R.id.layout_send_message),
                        childAtPosition(
                            IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
                            2)),
                    1),
                isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(
            allOf(withId(R.id.btn_send_message),
                childAtPosition(
                    allOf(withId(R.id.layout_send_message),
                        childAtPosition(
                            IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
                            2)),
                    1),
                isDisplayed()));
        button2.check(matches(isDisplayed()));
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
