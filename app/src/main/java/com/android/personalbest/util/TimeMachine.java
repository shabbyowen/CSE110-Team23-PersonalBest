package com.android.personalbest.util;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class TimeMachine {

    private static boolean timeFixed = false;
    private static boolean timeSet = false;
    private static long time = 0;
    private static final int CLOCK_CYCLE = 12;
    private static final int HOURS_IN_DAY = 24;
    private static Calendar cal = Calendar.getInstance();

    public static long nowMillis() {
        return DateCalculator.toLocalTime(nowCal().getTimeInMillis());
    }

    public static Calendar nowCal() {
        cal.setTimeZone(TimeZone.getDefault());
        if (timeSet){
            cal.setTimeInMillis(time);
        }
        else {
            cal = Calendar.getInstance();
        }
        return cal;
    }

    public static void freezeTime(long time) {
        timeFixed = true;
        TimeMachine.time = time;
    }

    public static void unfreezeTime() {
        timeFixed = false;
    }

    public static long setTime(long millis) {
        return DateCalculator.toLocalTime(millis);
    }

    public static void setTimeInDate(int year, int month, int day, int hour, int minutes, int seconds, boolean isPM) {
        if (isPM) {
            hour += CLOCK_CYCLE;
            hour = hour % HOURS_IN_DAY;
        }
        cal.set(year, month, day, hour, minutes, seconds);
        time = cal.getTimeInMillis();
        timeSet = true;
    }

    public static void setTimeInMillis(long millis) {
        cal.setTimeInMillis(millis);
        time = cal.getTimeInMillis();
        timeSet = true;
    }
}
