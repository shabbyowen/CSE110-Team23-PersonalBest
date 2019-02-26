package com.cse110.personalbest;

import com.cse110.personalbest.Utilities.DateCalculator;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class DateCalculatorUnitTest {

    @Test
    public void testIsSameDate() {
        Date date1 = new Date(1550615159000L);
        Date date2 = new Date(1550615159000L);
        Assert.assertTrue(DateCalculator.isSameDate(date1, date2));
    }

    @Test
    public void testIsSameDate2() {
        Date date1 = new Date(1550615159000L);
        Date date2 = new Date(1550615159000L + 86400 * 1000);
        Assert.assertFalse(DateCalculator.isSameDate(date1, date2));
    }

    // TODO: this test is broken in other time zone
    @Test
    public void testToClosestMidnightTmr() {
//        Date date = new Date(1550613647000L);
//        Date midnight = DateCalculator.toClosestMidnightTmr(date);
//        Assert.assertFalse(DateCalculator.isSameDate(date, midnight));
//        long expected = 1550649600000L;
//        long actual = midnight.getTime();
//        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDateDifference() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Date date1 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date date2 = cal.getTime();
        Assert.assertEquals(-1, DateCalculator.dateDifference(date1, date2));
    }

    @Test
    public void testDateDifference2() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Date date1 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, -99);
        Date date2 = cal.getTime();
        Assert.assertEquals(99, DateCalculator.dateDifference(date1, date2));
    }

    @Test
    public void testDateDifference3() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Date date1 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1000);
        Date date2 = cal.getTime();
        Assert.assertEquals(-1000, DateCalculator.dateDifference(date1, date2));
    }

    @Test
    public void testDateDifference4() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Date date1 = cal.getTime();
        Date date2 = cal.getTime();
        Assert.assertEquals(0, DateCalculator.dateDifference(date1, date2));
    }

    @Test
    public void testDateDifference5() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Date date1 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 400);
        Date date2 = cal.getTime();
        Assert.assertEquals(-400, DateCalculator.dateDifference(date1, date2));
    }

    @Test
    public void testDateDifferenceRandom() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int change = (random.nextInt() % 10000) - 5000;
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            Date date1 = cal.getTime();
            cal.add(Calendar.DAY_OF_YEAR, change);
            Date date2 = cal.getTime();
            Assert.assertEquals(-change, DateCalculator.dateDifference(date1, date2));
        }
    }
}
