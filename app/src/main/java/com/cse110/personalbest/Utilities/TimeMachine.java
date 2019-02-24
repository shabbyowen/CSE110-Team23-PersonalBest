package com.cse110.personalbest.Utilities;

import android.util.Log;

import java.util.Date;

public class TimeMachine {

    private static final String TAG = "TimeMachine";

    private static long fakeTime = -1;
    private static long lastSetTime = -1;

    public static Date now() {
        if (fakeTime < 0) {
            return new Date();
        } else {
            // update the fake time and return
            long delta = new Date().getTime() - lastSetTime;
            fakeTime = fakeTime + delta;
            lastSetTime = new Date().getTime();
            return new Date(fakeTime);
        }
    }

    public static long nowMillis() {
        return now().getTime();
    }

    public static void setTime(long millis) {
        if (millis < 0) {
            Log.e(TAG, "setTime received a negative time!");
            return;
        }
        fakeTime = millis;
        lastSetTime = new Date().getTime();
    }

    public static void unsetTime() {
        fakeTime = -1;
        lastSetTime = -1;
    }

    // TODO: write tests for this method
    public static String formatTime(int second) {
        int hour = 0;
        while (second >= 3600) {
            hour++;
            second -= 3600;
        }
        int minute = 0;
        while (second >= 60) {
            minute++;
            second -= 60;
        }
        StringBuilder sb = new StringBuilder();
        padZero(sb, hour);
        sb.append(':');
        padZero(sb, minute);
        sb.append(':');
        padZero(sb, second);
        return sb.toString();
    }

    private static void padZero(StringBuilder sb, int number) {
        if (number == 0) {
            sb.append("00");
            return;
        }
        if (number < 10) {
            sb.append(0);
        }
        sb.append(number);
    }
}
