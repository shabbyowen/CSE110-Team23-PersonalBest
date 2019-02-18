package com.android.personalbest;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.CombinedData;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.android.personalbest.R;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class WeeklyProgressFragmentTest {
    private CombinedChart testProgressChart;
    private CombinedData testCombinedData;
    private WeeklyProgressFragment testFragment;
    private FragmentManager testManager;
    private v4ToAppAdapter adapter;

    @Rule
    public ActivityTestRule<HomeScreenActivity> mainActivity = new ActivityTestRule<HomeScreenActivity>(HomeScreenActivity.class);

    @Before
    public void setUp() {
        testFragment = new WeeklyProgressFragment();

        adapter = new v4ToAppAdapter();
        adapter.setArguments(testFragment);

        mainActivity.getActivity().putFragment(testFragment);
        testManager = mainActivity.getActivity().getFragmentManager();
        /* FragmentTransaction testTransaction = testManager.beginTransaction();

        testTransaction.add(adapter, "WeeklyProgressTag");
        //testTransaction.detach(adapter).attach(adapter);
        testTransaction.commit();
        adapter.startActivity(mainActivity.getActivity().getIntent()); */
    }

    @Test
    public void testValuesForNotNull() {
        //assertNotNull(adapter.getV4Fragment());
        //assertNotNull(mainActivity.getActivity().getFragmentManager().getFragments().get(0));

        /*FragmentTransaction testTransaction = testManager.beginTransaction();

        testTransaction.add(adapter, "WeeklyProgressTag");
        //testTransaction.detach(adapter).attach(adapter);
        testTransaction.commit();
        mainActivity.getActivity().setFragmentManager(testManager);
        adapter.startActivity(mainActivity.getActivity().getIntent());

        assertNotNull(adapter.getActivity());*/
    }

    @Test
    public void onCreateView() {
        //testProgressChart = mainActivity.getActivity().getFragmentManager().getFragments()
//        assertEquals(testProgressChart.getXAxis().mAxisMaximum, testProgressChart.getData().getBarData().getXMax() + 0.75f);
//        assertEquals(testProgressChart.getXAxis().mAxisMinimum, testProgressChart.getData().getBarData().getXMax() - 0.75f);

    }
}