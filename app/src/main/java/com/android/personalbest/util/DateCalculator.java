package com.android.personalbest.util;

import java.util.Calendar;
import java.util.TimeZone;

public class DateCalculator {

    private static final long DAY_LENGTH_MILLISEC = 86400L * 1000L;

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
        return (int) Math.floor((toLocalTime(time) / DAY_LENGTH_MILLISEC));
    }

    public static Calendar toClosesetMinightTmr(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, 1);
        return cal;
    }

    public static long toClosesetMidnightTmr(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        toClosesetMinightTmr(cal);
        return DateCalculator.toLocalTime(cal.getTimeInMillis());
    }
}
