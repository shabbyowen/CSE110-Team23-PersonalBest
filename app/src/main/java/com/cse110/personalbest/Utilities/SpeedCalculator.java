package com.cse110.personalbest.Utilities;


// TODO: write tests
public class SpeedCalculator {

    private static final double STRIDE_LENGTH_CONST = 0.413;
    private static final double DEFAULT_HEIGHT = 70;

    public static double calculateSpeed(int step, int millis, int heightInInch) {
        double distance = stepToMiles(step, heightInInch);
        double time = (double) millis / 3600.0 / 1000.0; // convert to hours

        // avoid divide by zero error
        if (time == 0) {
            time = 1;
        }

        return distance / time;
    }

    public static double stepToMiles(int step, int heightInInch) {
        double distance = heightInInch * STRIDE_LENGTH_CONST * step;
        return distance / 12.0 / 5280.0; // convert to miles per steps
    }
}
