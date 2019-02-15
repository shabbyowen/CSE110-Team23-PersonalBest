package com.android.personalbest.util;

public class TimeMachine {

    private static boolean timeFixed = false;
    private static long time = 0;

    public static long nowMillis() {
        if (timeFixed) {
            return time;
        } else {
            return System.currentTimeMillis();
        }
    }

    public static void freezeTime(long time) {
        timeFixed = true;
        TimeMachine.time = time;
    }

    public static void unfreezeTime() {
        timeFixed = false;
    }
}
