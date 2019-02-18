package com.android.personalbest;

/**
 * this file contains the WeeklyProgressFragment that displays the weekly summary
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.personalbest.models.StepCounter;
import com.android.personalbest.models.WorkoutRecord;
import com.android.personalbest.util.DateCalculator;
import com.android.personalbest.util.SpeedCalculator;
import com.android.personalbest.util.TimeMachine;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Class to make the chart
 */
public class WeeklyProgressFragment extends Fragment {

    private CombinedChart progressChart;

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

    /**
     * This method is used to create the CombinedChart for display
     *
     * @param inflater as LayoutInflater
     * @param container as ViewGroup
     * @param savedInstanceState as Bundle
     * @return the fragment view for display
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_weekly_progress, container, false);

        progressChart = fragmentView.findViewById(R.id.progressChart);

        List<WorkoutRecord.Session> allSessions = weekRecords.getAggragatedSessions();

        int offset = this.offsetCalculator();
        this.findThisWeekSessions(allSessions, offset);

        return fragmentView;
    }

    /**
     * This method is used to draw the chart base on the data stored in sharedPref
     *
     * @param offset as int
     */
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

        // pushing goal data Note: This line may need to be changed to reflect the change of goals
        for (int i = 0; i < offset; i++) {
            goalDataList.add(new Entry(i, (float)StepCounter.getInstance(getActivity()).getGoal()));
        }

        // In case we are in the middle of the week, pad the rest of the line with the most recent goal
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
        // In case it's in the middle of the week, pad the rest of the day with 0
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

        //the midnight, that's the first millisecond of the next day.
        Long today = DateCalculator.toClosesetMinightTmr(TimeMachine.nowCal()).getTimeInMillis();
        today = DateCalculator.toLocalTime(today);

        intentionalStepsByDay = new int[offset];
        speedByDay = new double[offset];
        unIntentionalStepsByDay = new int[offset];

        // Counting intentional steps for every day
        int counter = 0;

        // There is the possibility that the session list is empty
        for (int i = 1; i <= offset && sessions.size() != 0; i++) {

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
                speedByDay[offset - i] = 0;
            }

        }


        ((HomeScreenActivity)getActivity()).fitnessService
                .updateStepCountWithCallback(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        List<Bucket> list = dataReadResponse.getBuckets();
                        list = list.subList(list.size()-offset, list.size());

                        //The leftmost DataPoint is the most recent steps for the day
                        for(int i = 0; i < offset; i++) {

                            int totalStepOfTheDay;

                            Log.d("WeeklyProgressFragment", list.toString());

                            DataSet totalStepOfTheDayDataSet = list.get(i)
                                .getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA);


                            // if the dataSet is empty there was no exercise for that day
                            if(totalStepOfTheDayDataSet.isEmpty()){
                                continue;
                            }else{
                                 totalStepOfTheDay = totalStepOfTheDayDataSet
                                         .getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                             }


                            WeeklyProgressFragment.this.unIntentionalStepsByDay[i] =
                                    totalStepOfTheDay -
                                            WeeklyProgressFragment.this.intentionalStepsByDay[i];
                        }

                        // The drawChart method has to be here to ensure the instances are populated before drawing
                        // the chart
                        drawChart(offset);
                    }
                });
    }
}
