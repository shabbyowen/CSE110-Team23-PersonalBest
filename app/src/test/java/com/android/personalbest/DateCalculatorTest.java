package com.android.personalbest;

import com.android.personalbest.util.DateCalculator;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateCalculatorTest {

    @Test
    public void testToLocalTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long gmt = cal.getTimeInMillis();
        long pst = gmt + TimeZone.getTimeZone("PST").getOffset(System.currentTimeMillis());
        Assert.assertEquals(pst, DateCalculator.toLocalTime(gmt));
    }

    @Test
    public void testToLocalDay() {
        Calendar cal = Calendar.getInstance();
        long time = 100L * 86400 * 1000;
        cal.setTimeInMillis(time);
        long pst = cal.getTimeInMillis() + TimeZone.getTimeZone("PST").getOffset(time);
        long expectedDays = pst / (86400L * 1000L);
        Assert.assertEquals(expectedDays, DateCalculator.toLocalEpochDay(pst));
    }

    @Test
    public void testDateChange() {
        long time1 = 100L * 86400 * 1000;
        long time2 = 100L * 86400 * 1000 + 1;
        Assert.assertFalse(DateCalculator.dateChanged(time1, time2));
    }

    @Test
    public void testDateChange2() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(100L * 86400 * 1000);
        long time1 = cal.getTimeInMillis();
        cal.add(Calendar.DATE, 1);
        long time2 = cal.getTimeInMillis();
        Assert.assertTrue(DateCalculator.dateChanged(time1, time2));
    }

    @Test
    public void testDateChange3() {
        long time1 = 1550476799000L;
        long time2 = 1550476800000L;
        Assert.assertTrue(DateCalculator.dateChanged(time1, time2));
    }
}
