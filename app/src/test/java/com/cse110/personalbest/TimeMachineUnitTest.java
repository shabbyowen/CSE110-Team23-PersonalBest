package com.cse110.personalbest;

import com.cse110.personalbest.Utilities.TimeMachine;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class TimeMachineUnitTest {

    @Before
    public void setup() {
        TimeMachine.unsetTime();
    }

    @Test
    public void testRealTime() {
        long actual = TimeMachine.nowMillis();
        long expected = new Date().getTime();
        Assert.assertTrue(Math.abs(expected - actual) < 100);
    }

    @Test
    public void testSetTime() {
        long expected = 1550613647000L;
        TimeMachine.setTime(expected);
        long actual = TimeMachine.nowMillis();
        Assert.assertTrue(Math.abs(expected - actual) < 100);
    }

    @Test
    public void testFakeTimeElapse() {
        long start = 1550613647000L;
        TimeMachine.setTime(start);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
        long end = TimeMachine.nowMillis();
        long actual = end - start;
        long expected = 3000;
        Assert.assertTrue(Math.abs(expected - actual) < 10);
    }
}
