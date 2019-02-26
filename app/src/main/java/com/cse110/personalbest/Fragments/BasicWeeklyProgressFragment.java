package com.cse110.personalbest.Fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cse110.personalbest.Events.WeeklyProgressFragmentInfo;
import com.cse110.personalbest.R;
import com.github.mikephil.charting.charts.CombinedChart;

public class BasicWeeklyProgressFragment extends WeeklyProgressFragment {


    private CombinedChart progressChat;

    public BasicWeeklyProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_basic_weekly_progress, container, false);
        progressChat = view.findViewById(R.id.fragment_basic_weekly_progress_chart);
        return view;
    }

    @Override
    public void updateView(WeeklyProgressFragmentInfo info) {

    }

    /*
    private void drawChartDummy(int offset) {

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
    */
}
