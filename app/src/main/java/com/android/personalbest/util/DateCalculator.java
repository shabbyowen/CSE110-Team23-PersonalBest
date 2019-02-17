package com.android.personalbest.util;

import java.util.Calendar;
import java.util.TimeZone;

public class DateCalculator {

    private static final long DAY_LENGTH_MILLISEC = 84600L * 1000L;

    public static boolean dateChanged(long startMillis, long endMillis) {
        long startDate = toLocalEpochDay(startMillis);
        long endDate = toLocalEpochDay(endMillis);
        return startDate != endDate;
    }

    public static long toLocalTime(long time) {
        long offset = TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET);
        return time + offset;
    }

    public static int toLocalEpochDay(long time) {
        return (int)(toLocalTime(time) / DAY_LENGTH_MILLISEC);
    }
}
