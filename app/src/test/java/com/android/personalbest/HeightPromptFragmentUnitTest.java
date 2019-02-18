package com.android.personalbest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class HeightPromptFragmentUnitTest {
    private HeightPromptFragment fragment;
    private HomeScreenActivity activity;

    @Before
    public void init() {
        activity = Robolectric.setupActivity(HomeScreenActivity.class);
        fragment = HeightPromptFragment.newInstance(activity, "INPUT_HEIGHT", R.string.prompt_height_str);
    }

    @Test
    public void notNull() {
        assertNotNull(fragment);
    }
}
