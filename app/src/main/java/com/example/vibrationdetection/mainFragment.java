package com.example.vibrationdetection;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.vibrationdetection.custom.DayAxisValueFormatter;
import com.example.vibrationdetection.custom.MyValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.XAxisRenderer;

import java.util.ArrayList;

public class mainFragment extends Fragment{
    private static final String TAG ="Main";
    private PageViewModel pageViewModel;
    BarChart barChart;

    public mainFragment() {

    }

    public static mainFragment newInstance(){
        return new mainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);

        pageViewModel.setIndex(TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.main, container, false);
        barChart =(BarChart) root.findViewById(R.id.barchart);


        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(1,44f));
        barEntries.add(new BarEntry(2,88f));
        barEntries.add(new BarEntry(3,66f));
        barEntries.add(new BarEntry(4,12f));
        barEntries.add(new BarEntry(5,19f));
        barEntries.add(new BarEntry(54,91f));
        BarDataSet barDataSet =new BarDataSet(barEntries,"Dates");
        ArrayList<String > theDates = new ArrayList<>();

        theDates.add("April");
        theDates.add("May");
        theDates.add("June");
        theDates.add("July");
        theDates.add("August");
        theDates.add("September");
        ValueFormatter custom = new MyValueFormatter("$");
        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(barChart);

        BarData theData = new BarData(barDataSet);
        theData.setBarWidth(0.9f); // set custom bar width
        barChart.setData(theData);

        YAxis left = barChart.getAxisLeft();
        left.setValueFormatter(custom);
        YAxis right = barChart.getAxisRight();
        right.setValueFormatter(custom);
        XAxis bottom = barChart.getXAxis();
        bottom.setValueFormatter(xAxisFormatter);

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setFitBars(true); // make the x-axis fit exactly all bars
        barChart.invalidate(); // refresh
        return root;
    }

}
