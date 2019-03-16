package com.cse110.personalbest.Fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cse110.personalbest.Events.WeeklyProgressFragmentInfo;
import com.cse110.personalbest.R;
import com.cse110.personalbest.Utilities.TimeMachine;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class BasicWeeklyProgressFragment extends WeeklyProgressFragment {


    private CombinedChart progressChart;
    private final int[] bar_colors = new int[]{Color.parseColor("#68a0b0"),Color.parseColor("#9178a0")};
    private String[] xAxisLabel1 = new String[]{"M", "T", "W", "Th", "F", "Sa", "S" };
    private String[] xAxisLabel2 = new String[]{"T", "W", "Th", "F", "Sa", "S", "M" };
    private String[] xAxisLabel3 = new String[]{"W", "Th", "F", "Sa", "S", "M", "T" };
    private String[] xAxisLabel4 = new String[]{"Th", "F", "Sa", "S", "M", "T", "W" };
    private String[] xAxisLabel5 = new String[]{"F", "Sa", "S", "M", "T", "W", "Th" };
    private String[] xAxisLabel6 = new String[]{"Sa", "S", "M", "T", "W", "Th", "F" };
    private String[] xAxisLabel7 = new String[]{"S", "M", "T", "W", "Th", "F", "Sa" };



    public BasicWeeklyProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_basic_weekly_progress, container, false);
        this.progressChart = view.findViewById(R.id.fragment_basic_weekly_progress_chart);
        return view;
    }

    @Override
    public void updateView(WeeklyProgressFragmentInfo info) {
        Description description = new Description();
        description.setText("Weekly Progress");
        this.progressChart.setDescription(description);

        BarDataSet barSet;

        Date today = TimeMachine.now();
        Calendar can = Calendar.getInstance();
        can.setTime(today);
        int day = can.get(Calendar.DAY_OF_WEEK);

        final String[] xAxisLabel;

        switch(day){
            case(1):
                xAxisLabel = this.xAxisLabel1;
                break;
            case(2):
                xAxisLabel = this.xAxisLabel2;
                break;
            case(3):
                xAxisLabel = this.xAxisLabel3;
                break;
            case(4):
                xAxisLabel = this.xAxisLabel4;
                break;
            case(5):
                xAxisLabel = this.xAxisLabel5;
                break;
            case(6):
                xAxisLabel = this.xAxisLabel6;
                break;
            default:
                xAxisLabel = this.xAxisLabel7;
                break;
        }


        List<BarEntry> yVals1 = new ArrayList<BarEntry>();
        for (int i = 0; i < info.intentionalSteps.size(); i++) {
            yVals1.add(new BarEntry(i, new float[]{info.intentionalSteps.get(i), info.unintentionalSteps.get(i)}));
        }

        barSet = new BarDataSet(yVals1, "");
        barSet.setDrawIcons(false);
        barSet.setStackLabels(new String[]{"Planned", "Other"});
        barSet.setValueTextSize(15);

        barSet.setColors(bar_colors);

        List<Entry> goalDataList = new ArrayList<>();

        // pushing goal data Note: This line may need to be changed to reflect the change of goals
        for (int i = 0; i < info.weekGoal.size(); i++) {
            goalDataList.add(new Entry(i, (float)info.weekGoal.get(i)));
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
        for (int i = 0; i < info.weekSpeed.size(); i++) {
            speedsList.add(new Entry(i, (float)info.weekSpeed.get(i)));
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
}
