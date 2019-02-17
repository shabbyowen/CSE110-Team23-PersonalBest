package com.android.personalbest;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.personalbest.models.StepCounter;
import com.android.personalbest.models.WorkoutRecord;
import com.android.personalbest.util.SpeedCalculator;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnSuccessListener;

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
    private int[] intentionalStepsByDay = null;
    private double[] speedByDay = null;
    private int[] unIntentionalStepsByDay = null;

    public WeeklyProgressFragment() {
        // Required empty public constructor
    }

    public void setProgressChart(CombinedChart progressChart) {this.progressChart = progressChart;}

    public CombinedChart getProgressChart() {return progressChart;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_weekly_progress, container, false);

        progressChart = fragmentView.findViewById(R.id.progressChart);

        List<WorkoutRecord.Session> allSessions = weekRecords.getAggragatedSessions();

        int offset = this.offsetCalculator();
        this.findThisWeekSessions(allSessions, offset);

        // Inflate the layout for this fragment
        //drawChart(offset);
        drawChartDummy(0);

        return fragmentView;
    }


    private void drawChart(int offset) {

        Description description = new Description();
        description.setText("Weekly Progress");
        progressChart.setDescription(description);

        List<BarEntry> yVals1 = new ArrayList<BarEntry>();


        // pushing step counts data for the week
        for (int i = 0; i < offset; i++) {
            yVals1.add(new BarEntry(i, new float[]{unIntentionalStepsByDay[i], intentionalStepsByDay[i]}));
        }
        for (int i = offset; i < 7; i++){
            yVals1.add(new BarEntry(i, new float[]{0, 0}));
        }

        BarDataSet barSet;

        barSet = new BarDataSet(yVals1, "");
        barSet.setDrawIcons(false);
        barSet.setStackLabels(new String[]{"Other", "Planned"});
        barSet.setValueTextSize(15);

        barSet.setColors(bar_colors);

        List<Entry> goalDataList = new ArrayList<>();

        // pushing goal data
        for (int i = 0; i < offset; i++) {
            goalDataList.add(new Entry(i, (float)StepCounter.getInstance(getActivity()).getGoal()));
        }
        for (int i = offset; i < 7; i++) {
            goalDataList.add(new BarEntry(i, (float)StepCounter.getInstance(getActivity()).getGoal()));
        }

        LineDataSet goalSet = new LineDataSet(goalDataList, "");

        goalSet.setLineWidth(2.5f);
        goalSet.setCircleColor(Color.rgb(240, 238, 70));
        goalSet.setCircleRadius(5f);
        goalSet.setColor(Color.rgb(240, 238, 70));
        goalSet.setLabel("Goal");
        goalSet.setValueTextSize(15);

        List<Entry> speedsList = new ArrayList<>();

        // pushing speed data
        for (int i = 0; i < offset; i++) {
            speedsList.add(new Entry(i, (float)speedByDay[i]));
        }
        for (int i = offset; i < 7; i++) {
            speedsList.add(new BarEntry(i, 0f));
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
        LineData lineData = new LineData(goalSet);
        LineData speedData = new LineData(speedSet);


        data.setData(barData);
        LineData chartData = new LineData();
        chartData.addDataSet(goalSet);
        chartData.addDataSet(speedSet);
        data.setData(chartData);
        data.setValueTextSize(15);

        progressChart.setData(data);
        progressChart.invalidate();
        progressChart.setScaleEnabled(false);

        progressChart.getXAxis().setAxisMaximum(barData.getXMax() + 0.75f);
        progressChart.getXAxis().setAxisMinimum(barData.getXMin() - 0.75f);


//        progressChart.getSecondScale().addSeries(series2);
//        // the y bounds are always manual for second scale
//        graph.getSecondScale().setMinY(0);
//        graph.getSecondScale().setMaxY(15);
//        series2.setColor(Color.RED);
//        graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.RED);

    }



    /**
     * This method is used to calculated the offset needed to find the correct sessions
     *
     * @return offset as int
     */
    private int offsetCalculator() {
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
     *
     * @param sessions all sessions stored in the app
     * @param offset   an offset used to calculate how far we need to go back
     */
    private void findThisWeekSessions(List<WorkoutRecord.Session> sessions, int offset) {


        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 23); //anything 0 - 23
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.setTimeZone(TimeZone.getDefault());

        Long today = c.getTimeInMillis(); //the midnight, that's the last second of the day.

        intentionalStepsByDay = new int[offset];
        speedByDay = new double[offset];
        unIntentionalStepsByDay = new int[offset];

        // Counting intentional steps for every day
        int counter = 0;

        for (int i = 1; i <= offset; i++) {

            //The rightmost session is the most recent session
            WorkoutRecord.Session daySession = sessions.get(sessions.size() - counter - 1);
            //Checking if the sessions is within the numerical range of a specific day
            if (today - i * numMillInDay < daySession.startTime && daySession.startTime <= today - (i - 1) * numMillInDay) {
                intentionalStepsByDay[offset - i] = daySession.deltaStep;
                speedByDay[offset - i] =
                        SpeedCalculator.calculateSpeed(daySession.deltaStep, (int) daySession.deltaTime);

                counter++;
            } else {
                intentionalStepsByDay[offset - i] = 0;
                speedByDay[offset - i] =
                        SpeedCalculator.calculateSpeed(daySession.deltaStep, (int) daySession.deltaTime);
            }

        }


        ((HomeScreenActivity)getActivity()).fitnessService
                .updateStepCountWithCallback(new OnSuccessListener<com.google.android.gms.fitness.data.DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        List<DataPoint> list = dataSet.getDataPoints().subList(0, offset);

                        //The leftmost DataPoint is the most recent steps for the day
                        for(int i = 0; i < offset; i++) {
                            WeeklyProgressFragment.this.unIntentionalStepsByDay[offset - i - 1] =
                                    list.get(i).getValue(Field.FIELD_STEPS).asInt() -
                                            WeeklyProgressFragment.this.intentionalStepsByDay[offset - i - 1];
                        }
                    }
                });
    }

    public void drawChartDummy(int offset) {

        Description description = new Description();
        description.setText("Weekly Progress");
        progressChart.setDescription(description);

        List<BarEntry> yVals1 = new ArrayList<BarEntry>();


        // pushing step counts data for the week
        yVals1.add(new BarEntry(0, new float[]{3500, 1200}));
        yVals1.add(new BarEntry(1, new float[]{1400, 2500}));
        yVals1.add(new BarEntry(2, new float[]{6500, 500}));
        yVals1.add(new BarEntry(3, new float[]{4000, 2100}));
        yVals1.add(new BarEntry(4, new float[]{3500, 3600}));
        yVals1.add(new BarEntry(5, new float[]{2000, 5700}));
        yVals1.add(new BarEntry(6, new float[]{0,0}));

        BarDataSet barSet;

        barSet = new BarDataSet(yVals1, "");
        barSet.setDrawIcons(false);
        barSet.setStackLabels(new String[]{"Other", "Planned"});
        barSet.setValueTextSize(15);

        barSet.setColors(bar_colors);

        List<Entry> goalDataList = new ArrayList<>();

        // pushing goal data
        goalDataList.add(new Entry(0, 5000));
        goalDataList.add(new Entry(1, 5000));
        goalDataList.add(new Entry(2, 5500));
        goalDataList.add(new Entry(3, 5500));
        goalDataList.add(new Entry(4, 6000));
        goalDataList.add(new Entry(5, 6500));
        goalDataList.add(new Entry(6, 6500));


        LineDataSet goalSet = new LineDataSet(goalDataList, "");

        goalSet.setLineWidth(2.5f);
        goalSet.setCircleColor(Color.rgb(240, 238, 70));
        goalSet.setCircleRadius(5f);
        goalSet.setColor(Color.rgb(240, 238, 70));
        goalSet.setLabel("Goal");
        goalSet.setValueTextSize(15);

        List<Entry> speedsList = new ArrayList<>();

        // pushing speed data
        speedsList.add(new BarEntry(0, 4f));
        speedsList.add(new BarEntry(1, 2f));
        speedsList.add(new BarEntry(2, 1f));
        speedsList.add(new BarEntry(3, 4f));
        speedsList.add(new BarEntry(4, 3f));
        speedsList.add(new BarEntry(5, 6f));
        speedsList.add(new BarEntry(6, 0f));



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
        LineData lineData = new LineData(goalSet);
        LineData speedData = new LineData(speedSet);


        data.setData(barData);
        LineData chartData = new LineData();
        chartData.addDataSet(goalSet);
        chartData.addDataSet(speedSet);
        data.setData(chartData);
        data.setValueTextSize(15);

        progressChart.setData(data);
        progressChart.invalidate();
        progressChart.setScaleEnabled(false);

        progressChart.getXAxis().setAxisMaximum(barData.getXMax() + 0.75f);
        progressChart.getXAxis().setAxisMinimum(barData.getXMin() - 0.75f);


//        progressChart.getSecondScale().addSeries(series2);
//        // the y bounds are always manual for second scale
//        graph.getSecondScale().setMinY(0);
//        graph.getSecondScale().setMaxY(15);
//        series2.setColor(Color.RED);
//        graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.RED);

    }
}
