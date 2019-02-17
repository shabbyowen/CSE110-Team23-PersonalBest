package com.android.personalbest.util;

public class DateCalculator {

    private static final long DAY_LENGTH_MILLISEC = 84600L * 1000L;

    public static boolean dateChanged(long startMillis, long endMillis) {
        long startDate = startMillis / DAY_LENGTH_MILLISEC;
        long currentDate = endMillis / DAY_LENGTH_MILLISEC;
        return startDate != currentDate;
    }
}
