package com.android.personalbest;

import com.android.personalbest.util.DateCalculator;
import junit.framework.Assert;
import org.junit.Test;

public class DateCalculatorTest {

    @Test
    public void dateChangedTest(){

        //Feb 17th 2019 11:59:59 PM PCT
        long startTime = 1550476799000L;

        //Feb 18th 2019 12:00:00 AM PCT
        long endTime = 1550476800000L;

        startTime = 1550473259000L;

        Assert.assertTrue(DateCalculator.dateChanged(startTime, endTime));
    }

}
