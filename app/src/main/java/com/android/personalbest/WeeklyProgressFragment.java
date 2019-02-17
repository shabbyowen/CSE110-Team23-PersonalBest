package com.android.personalbest;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.personalbest.models.WorkoutRecord;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 */
public class WeeklyProgressFragment extends Fragment {

    private BarChart barChart;
    private final int[] bar_colors = new int[]{ColorTemplate.JOYFUL_COLORS[0],ColorTemplate.JOYFUL_COLORS[1]};
    private final String[] xAxisLabel = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private WorkoutRecord weekRecords = WorkoutRecord.getInstance(getContext());

    public WeeklyProgressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_weekly_progress, container, false);

        barChart = fragmentView.findViewById(R.id.barChart);

        List<WorkoutRecord.Session> allSessions = weekRecords.getSessions();

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
            yVals1.add(new BarEntry(i, new float[]{1f,2f}));
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
}
