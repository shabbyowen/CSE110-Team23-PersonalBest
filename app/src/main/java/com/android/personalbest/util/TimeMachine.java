package com.android.personalbest.util;

import java.util.Calendar;
import java.util.TimeZone;

public class TimeMachine {

    private static boolean timeFixed = false;
    private static long time = 0;

    public static long nowMillis() {
        return DateCalculator.toLocalTime(nowCal().getTimeInMillis());
    }

    public static Calendar nowCal() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        return cal;
    }

    public static void freezeTime(long time) {
        timeFixed = true;
        TimeMachine.time = time;
    }

    public static void unfreezeTime() {
        timeFixed = false;
    }
}
