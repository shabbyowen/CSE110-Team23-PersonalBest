package com.android.personalbest;

import com.android.personalbest.util.SpeedCalculator;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SpeedCalculatorTest {

    private HomeScreenActivity activity;
    private int height = 70;

    @Before
    public void init(){
        activity = Robolectric.setupActivity(HomeScreenActivity.class);
    }


    @Test
    public void stepsToMilesTest(){
        int steps = 1000;
        double result = steps*height*0.413/ 12.0 / 5280.0;

        //Testing the arithmetic accuracy of the method
        Assert.assertEquals(result, SpeedCalculator.stepToMiles(1000));

    }

    @Test
    public void calculatedSpeedTest(){
        int steps = 1000;
        int milliseconds = 36000000;
        double time = (double) milliseconds / 1000.0 / 3600.0; // convert to hours
        double result = SpeedCalculator.stepToMiles(1000)/time;

        //Testing the scenario when time elapsed is non-zero
        Assert.assertEquals(result, SpeedCalculator.calculateSpeed(steps,milliseconds));

        //Testing the scenario when time elapsed is zero
        result = SpeedCalculator.stepToMiles(1000);
        Assert.assertEquals(result, SpeedCalculator.calculateSpeed(steps, 0));
    }

}
