package com.example.myapplication.UI.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.Order;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
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
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class RevenueReportActivity extends AppCompatActivity {

    private TextView tvRevenue, tvOrders, tvAvgValue, tvCustomers;
    private Spinner spinnerFilter;
    private LineChart lineChart;
    private PieChart pieChart;
    private BarChart barChart;

    private DatabaseReference ordersRef;
    private List<Order> allOrders = new ArrayList<>();
    
    // Format tiền tệ Việt Nam
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,### đ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_report);

        initViews();
        setupToolbar();
        setupSpinner();

        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        loadDataFromFirebase();
    }

    private void initViews() {
        tvRevenue = findViewById(R.id.tvRevenue);
        tvOrders = findViewById(R.id.tvOrders);
        tvAvgValue = findViewById(R.id.tvAvgValue);
        tvCustomers = findViewById(R.id.tvCustomers);
        spinnerFilter = findViewById(R.id.spinnerFilter);
        lineChart = findViewById(R.id.lineChart);
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupSpinner() {
        String[] filters = {"Tất cả", "Tháng này", "Tuần này", "Hôm nay"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filters);
        spinnerFilter.setAdapter(adapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterData(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleEmptyData() {
        if (allOrders.isEmpty()) {
            tvRevenue.setText("Không có dữ liệu");
            tvOrders.setText("0");
            tvAvgValue.setText("0 đ");
            tvCustomers.setText("0");
            lineChart.clear();
            pieChart.clear();
            barChart.clear();
        }
    }

    private void loadDataFromFirebase() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allOrders.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    try {
                        Order order = data.getValue(Order.class);
                        if (order != null) {
                            allOrders.add(order);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (allOrders.isEmpty()) {
                    handleEmptyData();
                } else {
                    filterData(spinnerFilter.getSelectedItemPosition());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RevenueReportActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterData(int filterType) {
        List<Order> filteredList = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTimeInMillis(currentTime);
        int currentYear = currentCal.get(Calendar.YEAR);
        int currentMonth = currentCal.get(Calendar.MONTH);
        int currentWeek = currentCal.get(Calendar.WEEK_OF_YEAR);
        int currentDay = currentCal.get(Calendar.DAY_OF_YEAR);

        for (Order order : allOrders) {
            boolean include = false;
            long orderTime = order.getOrderDate();
            
            Calendar orderCal = Calendar.getInstance();
            orderCal.setTimeInMillis(orderTime);
            int orderYear = orderCal.get(Calendar.YEAR);
            int orderMonth = orderCal.get(Calendar.MONTH);
            int orderWeek = orderCal.get(Calendar.WEEK_OF_YEAR);
            int orderDay = orderCal.get(Calendar.DAY_OF_YEAR);

            switch (filterType) {
                case 0: // Tất cả
                    include = true;
                    break;
                case 1: // Tháng này
                    if (currentYear == orderYear && currentMonth == orderMonth) {
                        include = true;
                    }
                    break;
                case 2: // Tuần này
                    if (currentYear == orderYear && currentWeek == orderWeek) {
                        include = true;
                    }
                    break;
                case 3: // Hôm nay
                    if (currentYear == orderYear && currentDay == orderDay) {
                        include = true;
                    }
                    break;
            }

            if (include) {
                filteredList.add(order);
            }
        }

        processStatistics(filteredList);
    }

    private void processStatistics(List<Order> orders) {
        double totalRevenue = 0;
        int totalOrders = orders.size();
        Set<String> uniqueCustomers = new HashSet<>();

        // Map cho LineChart: Ngày (yyyy-MM-dd) -> Doanh thu
        // Sử dụng key yyyy-MM-dd để TreeMap sắp xếp đúng thứ tự thời gian
        Map<String, Double> revenueByDate = new TreeMap<>(); 

        // Map cho PieChart: Trạng thái -> Số lượng
        Map<String, Integer> statusCount = new HashMap<>();

        // Map cho BarChart: Tên món -> Số lượng
        Map<String, Integer> productCount = new HashMap<>();

        SimpleDateFormat sortDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (Order order : orders) {
            // 1. Tổng quan
            totalRevenue += order.getTotalAmount();
            
            // Đếm khách hàng
            String customerId = order.getUserId();
            if (customerId == null || customerId.isEmpty()) {
                customerId = order.getPhoneNumber();
            }
            if (customerId != null && !customerId.isEmpty()) {
                uniqueCustomers.add(customerId);
            }

            // 2. LineChart Data
            if (order.getOrderDate() > 0) {
                String dateKey = sortDateFormat.format(new Date(order.getOrderDate()));
                revenueByDate.put(dateKey, revenueByDate.getOrDefault(dateKey, 0.0) + order.getTotalAmount());
            }

            // 3. PieChart Data
            String status = order.getStatus();
            if (status == null) status = "Không xác định";
            statusCount.put(status, statusCount.getOrDefault(status, 0) + 1);

            // 4. BarChart Data (Top sản phẩm)
            if (order.getCartItems() != null) {
                for (CartItem item : order.getCartItems()) {
                    // Ưu tiên getProductName, nếu null dùng getName (compatibility)
                    String productName = item.getProductName(); 
                    if (productName == null) productName = item.getName();
                    
                    if (productName != null) {
                        productCount.put(productName, productCount.getOrDefault(productName, 0) + item.getQuantity());
                    }
                }
            }
        }

        // Cập nhật UI Text
        tvRevenue.setText(decimalFormat.format(totalRevenue));
        tvOrders.setText(String.valueOf(totalOrders));
        tvCustomers.setText(String.valueOf(uniqueCustomers.size()));
        
        double avgValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;
        tvAvgValue.setText(decimalFormat.format(avgValue));

        // Vẽ biểu đồ
        setupLineChart(revenueByDate);
        setupPieChart(statusCount);
        setupBarChart(productCount);
    }

    private void setupLineChart(Map<String, Double> data) {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        
        SimpleDateFormat sortDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
        
        int index = 0;
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            entries.add(new Entry(index, entry.getValue().floatValue()));
            
            // Convert yyyy-MM-dd to dd/MM for display
            String label = entry.getKey();
            try {
                Date date = sortDateFormat.parse(entry.getKey());
                if (date != null) {
                    label = displayDateFormat.format(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            labels.add(label);
            index++;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Doanh thu");
        dataSet.setColor(Color.parseColor("#4E342E")); // Màu nâu chủ đạo
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.parseColor("#4E342E"));
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false); 
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#D7CCC8")); // Nâu nhạt

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45);

        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.animateY(1000);
        lineChart.invalidate();
    }

    private void setupPieChart(Map<String, Integer> data) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Trạng thái");
        pieChart.setCenterTextSize(14f);
        
        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);
        pieChart.getLegend().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void setupBarChart(Map<String, Integer> data) {
        // Sắp xếp giảm dần để lấy Top 5
        List<Map.Entry<String, Integer>> list = new ArrayList<>(data.entrySet());
        Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        
        int count = 0;
        for (Map.Entry<String, Integer> entry : list) {
            if (count >= 5) break; 
            entries.add(new BarEntry(count, entry.getValue()));
            
            String name = entry.getKey();
            if (name.length() > 10) name = name.substring(0, 8) + "..";
            labels.add(name);
            count++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Số lượng");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-15);
        
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);
        barChart.getAxisRight().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }
}
