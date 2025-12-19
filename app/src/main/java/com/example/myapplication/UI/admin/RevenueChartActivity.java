package com.example.myapplication.UI.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class RevenueChartActivity extends AppCompatActivity {

    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_chart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        barChart = findViewById(R.id.barChartFull);
        setupBarChart();
    }

    private void setupBarChart() {
        // Dữ liệu giả cho 7 ngày
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 1500000f));
        entries.add(new BarEntry(1, 2300000f));
        entries.add(new BarEntry(2, 1800000f));
        entries.add(new BarEntry(3, 3200000f));
        entries.add(new BarEntry(4, 2100000f));
        entries.add(new BarEntry(5, 4500000f));
        entries.add(new BarEntry(6, 3800000f));

        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu (VNĐ)");
        dataSet.setColor(Color.parseColor("#673AB7")); // Màu tím theo yêu cầu
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        String[] days = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }
}
