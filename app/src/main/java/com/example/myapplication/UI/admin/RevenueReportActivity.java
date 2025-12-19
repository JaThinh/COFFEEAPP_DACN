package com.example.myapplication.UI.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RevenueReportActivity extends AppCompatActivity {

    private LineChart lineChart;
    private PieChart pieChart;
    private HorizontalBarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_report);

        initToolbar();
        initViews();
        setupLineChart();
        setupPieChart();
        setupBarChart();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    private void initViews() {
        lineChart = findViewById(R.id.lineChart);
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.horizontalBarChart);
    }

    private void setupLineChart() {
        // Mock data for 30 days
        List<Entry> entries = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            // Random revenue between 500k and 3M
            float val = (float) (Math.random() * 2500000) + 500000;
            if (i % 7 == 0) val += 2000000; // Peaks on weekends
            entries.add(new Entry(i, val));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Doanh thu (VNĐ)");
        dataSet.setColor(Color.parseColor("#673AB7")); // Deep Purple
        dataSet.setCircleColor(Color.parseColor("#673AB7"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#D1C4E9"));
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Styling
        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setGranularity(5f); // Show every 5 days roughly
        
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(value);
            }
        });

        lineChart.animateX(1000);
        lineChart.invalidate();
    }

    private void setupPieChart() {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(60f, "Đã giao"));
        entries.add(new PieEntry(30f, "Đã hủy"));
        entries.add(new PieEntry(10f, "Chờ xử lý"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(
                Color.parseColor("#4CAF50"), // Green
                Color.parseColor("#F44336"), // Red
                Color.parseColor("#FFC107")  // Amber
        );
        dataSet.setSliceSpace(3f);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        // Styling
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setHoleRadius(40f); // Donut style
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setCenterText("Đơn hàng");
        pieChart.setCenterTextSize(14f);
        
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void setupBarChart() {
        // Top 5 Products
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 150));
        entries.add(new BarEntry(1, 120));
        entries.add(new BarEntry(2, 90));
        entries.add(new BarEntry(3, 80));
        entries.add(new BarEntry(4, 60));

        BarDataSet dataSet = new BarDataSet(entries, "Đã bán");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);
        barChart.setData(barData);

        // X Axis Labels (Product Names)
        String[] products = new String[]{"Cà phê sữa", "Trà đào", "Bạc xỉu", "Trà vải", "Cookie đá xay"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(products));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Handle Back button
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
