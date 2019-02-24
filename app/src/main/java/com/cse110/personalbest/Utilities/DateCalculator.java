package com.cse110.personalbest.Utilities;

import java.util.Calendar;
import java.util.Date;

public class DateCalculator {

    public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    // date1 - date2
    public static int dateDifference(Date date1, Date date2) {
        long time1 = toClosestMidnightTmr(date1).getTime();
        long time2 = toClosestMidnightTmr(date2).getTime();
        double diff = time1 - time2;
        return (int)Math.round(diff / (86400.0 * 1000.0));
    }

    public static Date toClosestMidnightTmr(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        return cal.getTime();
    }
}
