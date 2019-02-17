package com.android.personalbest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.personalbest.models.WorkoutRecord;
import com.android.personalbest.util.SpeedCalculator;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeeklyProgressFragment extends Fragment {

    private BarChart barChart;
    private final int[] bar_colors = new int[]{ColorTemplate.JOYFUL_COLORS[0],ColorTemplate.JOYFUL_COLORS[1]};
    private final String[] xAxisLabel = new String[]{ "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private WorkoutRecord weekRecords = WorkoutRecord.getInstance(getContext());
    private final long numMillInDay = 86400000;
    private int[] stepsByDay = null;
    private long[] deltaTimeByDay = null;
    private double[] speedByDay = null;



    public WeeklyProgressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_weekly_progress, container, false);

        barChart = fragmentView.findViewById(R.id.barChart);

        List<WorkoutRecord.Session> allSessions = weekRecords.getSessions();

        int offset = this.offsetCalculator();
        this.findThisWeekSessions(allSessions, offset);

        // Inflate the layout for this fragment
        drawChart();

        return fragmentView;
    }


    private void drawChart() {

        Description description = new Description();
        description.setText("Weekly Progress");
        barChart.setDescription(description);

        int numberOfDays = 7;
        List<BarEntry> yVals1 = new ArrayList<BarEntry>();



        for (int i = 0; i < numberOfDays; i++) {
            yVals1.add(new BarEntry(i, new float[]{500f,300f}));
        }

        BarDataSet set1;

        set1 = new BarDataSet(yVals1, "Weekly Workout");
        set1.setDrawIcons(false);
        set1.setStackLabels(new String[]{"Intentional", "Unintentional"});

        set1.setColors(bar_colors);

        //Fixing the X-axis to Weekdays
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xAxisLabel[(int) value];
            }
        });

        //Fixing the left and right axises to integer
        barChart.getAxisLeft().setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) Math.floor(value));
            }
        });


        barChart.getAxisRight().setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) Math.floor(value));
            }
        });

        BarData data = new BarData(set1);
        barChart.setData(data);
        barChart.setFitBars(true);
        barChart.invalidate();

    }

    /**
     * This method is used to calculated the offset needed to find the correct sessions
     * @return offset as int
     */
    private int offsetCalculator(){
        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.SUNDAY:
                return 1;

            case Calendar.MONDAY:
                return 2;

            case Calendar.TUESDAY:
                return 3;

            case Calendar.WEDNESDAY:
                return 4;

            case Calendar.THURSDAY:
                return 5;

            case Calendar.FRIDAY:
                return 6;

            case Calendar.SATURDAY:
                return 7;
            default:
                throw new RuntimeException("Day is missing");
        }
    }

    /**
     * This method is used to find the workout sessions in the current week
     * @param sessions all sessions stored in the app
     * @param offset an offset used to calculate how far we need to go back
     */
    private void findThisWeekSessions( List<WorkoutRecord.Session> sessions, int offset){


        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 23); //anything 0 - 23
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);

        Long today = c.getTimeInMillis(); //the midnight, that's the last second of the day.

        stepsByDay = new int[offset];
        deltaTimeByDay = new long[offset];
        speedByDay = new double[offset];

        int counter = 0;
        for(int i = 1; i <= offset; i++){

            WorkoutRecord.Session daySession = sessions.get(sessions.size()-counter);
            //Checking if the sessions is within the numerical range of a specific day
            if (today - i*numMillInDay < daySession.startTime && daySession.startTime <= today - (i-1)*numMillInDay){
                stepsByDay[offset-1-counter] = daySession.deltaStep;
                deltaTimeByDay[offset-1-counter] = daySession.deltaTime;
                speedByDay[offset-1-counter] =
                        SpeedCalculator.calculateSpeed(daySession.deltaStep, (int)daySession.deltaTime);
            }
        }
    }
}
