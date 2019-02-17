package com.android.personalbest;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.personalbest.models.WorkoutRecord;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.*;
import com.android.personalbest.util.SpeedCalculator;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.*;



/**
 * A simple {@link Fragment} subclass.
 */
public class WeeklyProgressFragment extends Fragment {

    private CombinedChart progressChart;
    //private BarChart progressChart;
    private final int[] bar_colors = new int[]{Color.parseColor("#68a0b0"),Color.parseColor("#9178a0")};
    private final String[] xAxisLabel = new String[]{ "S", "M", "T", "W", "T", "F", "S" };
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

        progressChart = fragmentView.findViewById(R.id.progressChart);

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
        progressChart.setDescription(description);

        int numberOfDays = 7;
        List<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < numberOfDays; i++) {
            yVals1.add(new BarEntry(i, new float[]{100f, 200f}));
        }

        BarDataSet barSet;

        barSet = new BarDataSet(yVals1, "");
        barSet.setDrawIcons(false);
        barSet.setStackLabels(new String[]{"Other", "Planned"});
        barSet.setValueTextSize(15);

        barSet.setColors(bar_colors);

        List<Entry> lineDataList = new ArrayList<>();

        // set line dummy data
        for (int i = 0; i < numberOfDays; i++) {
            lineDataList.add(new Entry(i, 250));
        }

        LineDataSet lineSet = new LineDataSet(lineDataList, "");

        lineSet.setLineWidth(2.5f);
        lineSet.setCircleColor(Color.rgb(240, 238, 70));
        lineSet.setCircleRadius(5f);
        lineSet.setColor(Color.rgb(240, 238, 70));
        lineSet.setLabel("Goal");
        lineSet.setValueTextSize(15);

        List<Entry> speedsList = new ArrayList<>();
        // set speed dummy data
        for (int i = 0; i < numberOfDays; i++) {
            speedsList.add(new Entry(i, 5));
        }

        LineDataSet speedSet = new LineDataSet(speedsList, "");

        speedSet.setLineWidth(2.5f);
        speedSet.setCircleColor(Color.BLUE);
        speedSet.setCircleRadius(5f);
        speedSet.setColor(Color.BLUE);
        speedSet.setLabel("Speed");
        speedSet.setValueTextSize(15);


        //Fixing the X-axis to Weekdays
        progressChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xAxisLabel[(int) value];
            }
        });

        //Fixing the left and right axises to integer
        progressChart.getAxisLeft().setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) Math.floor(value));
            }
        });


        progressChart.getAxisRight().setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) Math.floor(value));
            }
        });

        CombinedData data = new CombinedData();
        BarData barData = new BarData(barSet);
        LineData lineData = new LineData(lineSet);
        LineData speedData = new LineData(speedSet);


        data.setData(barData);
        LineData chartData = new LineData();
        chartData.addDataSet(lineSet);
        chartData.addDataSet(speedSet);
        data.setData(chartData);
        data.setValueTextSize(15);

        progressChart.setData(data);
        //progressChart.setFitBars(true);
        progressChart.invalidate();
        progressChart.setScaleEnabled(false);

        progressChart.getXAxis().setAxisMaximum(barData.getXMax() + 0.75f);
        progressChart.getXAxis().setAxisMinimum(barData.getXMin() - 0.75f);

        progressChart.getXAxis().setTextSize(30);


//        progressChart.getSecondScale().addSeries(series2);
//        // the y bounds are always manual for second scale
//        graph.getSecondScale().setMinY(0);
//        graph.getSecondScale().setMaxY(15);
//        series2.setColor(Color.RED);
//        graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.RED);

    }



    /**
     * This method is used to calculated the offset needed to find the correct sessions
     * @return offset as int
     */
    private int offsetCalculator(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());

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
        c.setTimeZone(TimeZone.getDefault());

        Long today = c.getTimeInMillis(); //the midnight, that's the last second of the day.

        stepsByDay = new int[offset];
        deltaTimeByDay = new long[offset];
        speedByDay = new double[offset];

        int counter = 0;

//        for(int i = 1; i <= offset; i++) {
//
//            WorkoutRecord.Session daySession = sessions.get(sessions.size() - counter);
//            //Checking if the sessions is within the numerical range of a specific day
//            if (today - i * numMillInDay < daySession.startTime && daySession.startTime <= today - (i - 1) * numMillInDay) {
//                stepsByDay[offset - 1 - counter] = daySession.deltaStep;
//                deltaTimeByDay[offset - 1 - counter] = daySession.deltaTime;
//                speedByDay[offset - 1 - counter] =
//                        SpeedCalculator.calculateSpeed(daySession.deltaStep, (int) daySession.deltaTime);
//            } else {
//
//            }
//        }
    }
}
