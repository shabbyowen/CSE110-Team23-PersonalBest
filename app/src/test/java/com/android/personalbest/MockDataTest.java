package com.android.personalbest;

import com.android.personalbest.util.MockData;
import com.google.android.gms.fitness.data.Field;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import com.google.android.gms.fitness.data.DataSet;


@RunWith(RobolectricTestRunner.class)
public class MockDataTest {
    private HomeScreenActivity activity;

    @Before
    public void init(){
        activity = Robolectric.setupActivity(HomeScreenActivity.class);
    }

    @Test
    public void basicMockDataTest(){

        // Basic tests to verify the dataSet is storing the correct information
        int step = 0;
        DataSet canvas = MockData.mockFitnessData(activity, step);
        int result = canvas.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();

        Assert.assertEquals(result, step);

        // Note that Google fitness API doesn't allow the user to go over 100 meters per second
        step = 300;
        canvas = MockData.mockFitnessData(activity, step);
        result = canvas.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();

        Assert.assertEquals(result, step);


    }
}
