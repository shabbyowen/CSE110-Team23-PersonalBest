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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 */
public class WeeklyProgressFragment extends Fragment {

    private CombinedChart progressChart;
    //private BarChart progressChart;
    private final int[] bar_colors = new int[]{Color.parseColor("#68a0b0"),Color.parseColor("#9178a0")};
    private final String[] xAxisLabel = new String[]{ "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
    private WorkoutRecord weekRecords = WorkoutRecord.getInstance(getContext());

    public WeeklyProgressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_weekly_progress, container, false);

        progressChart = fragmentView.findViewById(R.id.progressChart);

        List<WorkoutRecord.Session> allSessions = weekRecords.getSessions();

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
            yVals1.add(new BarEntry(i, new float[]{10f,20f}));
        }

        BarDataSet barSet;

        barSet = new BarDataSet(yVals1, "");
        barSet.setDrawIcons(false);
        barSet.setStackLabels(new String[]{"Other", "Planned"});
        barSet.setColors(bar_colors);

        List<Entry> lineDataList = new ArrayList<>();

        // set line dummy data
        for (int i = 0; i < numberOfDays; i++) {
            lineDataList.add(new Entry(i, 25));
        }

        LineDataSet lineSet = new LineDataSet(lineDataList, "");

        lineSet.setLineWidth(2.5f);
        lineSet.setCircleColor(Color.rgb(240, 238, 70));
        lineSet.setCircleRadius(5f);
        lineSet.setColor(Color.rgb(240, 238, 70));
        lineSet.setLabel("Goal");


        //Fixing the X-axis to Weekdays
        progressChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xAxisLabel[(int) value];
            }
        });

        //Fixing the left and right axises to integer
        progressChart.getAxisLeft().setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) Math.floor(value));
            }
        });


        progressChart.getAxisRight().setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) Math.floor(value));
            }
        });

        CombinedData data = new CombinedData();
        BarData barData = new BarData(barSet);
        LineData lineData = new LineData(lineSet);

        data.setData(barData);
        data.setData(lineData);

        progressChart.setData(data);
        //progressChart.setFitBars(true);
        progressChart.invalidate();
        progressChart.setScaleEnabled(false);

        progressChart.getXAxis().setAxisMaximum(barData.getXMax() + 0.75f);
        progressChart.getXAxis().setAxisMinimum(barData.getXMin() - 0.75f);

    }
}
