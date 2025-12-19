package com.example.myapplication.UI.admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myapplication.R;
import com.example.myapplication.model.Order;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView tvTotalRevenue, tvOrdersToday;
    private BarChart barChart;
    
    // Cards Menu
    private CardView cardProducts, cardCategories, cardOrders, cardCustomers;
    private CardView cardReports, cardInvoice, cardBanners, cardReviews;

    private DatabaseReference ordersRef;
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,### đ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        setupToolbar();
        setupEvents();
        
        // Sử dụng tham chiếu đến node "Orders" để đồng bộ với phần báo cáo (RevenueReportActivity)
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        loadDataFromFirebase();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvOrdersToday = findViewById(R.id.tvOrdersToday);
        barChart = findViewById(R.id.barChart);
        
        // Map Menu Cards
        cardProducts = findViewById(R.id.cardProducts);
        cardCategories = findViewById(R.id.cardCategories);
        cardOrders = findViewById(R.id.cardOrders);
        cardCustomers = findViewById(R.id.cardCustomers);
        cardReports = findViewById(R.id.cardReports);
        cardInvoice = findViewById(R.id.cardInvoice);
        cardBanners = findViewById(R.id.cardBanners);
        cardReviews = findViewById(R.id.cardReviews);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, 
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    private void setupEvents() {
        // 1. Quản lý Sản phẩm
        cardProducts.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminProductListActivity.class));
        });

        // 2. Quản lý Danh mục
        cardCategories.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminCategoryListActivity.class));
        });

        // 3. Quản lý Đơn hàng
        cardOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminOrdersActivity.class));
        });

        // 4. Quản lý Khách hàng
        cardCustomers.setOnClickListener(v -> {
            startActivity(new Intent(this, ManageUsersActivity.class));
        });

        // 5. Báo cáo & Thống kê
        cardReports.setOnClickListener(v -> {
            startActivity(new Intent(this, RevenueReportActivity.class));
        });

        // 6. Xuất Hóa đơn (Chuyển tới Activity in bill hoặc list để chọn đơn)
        cardInvoice.setOnClickListener(v -> {
             startActivity(new Intent(this, InvoiceActivity.class));
        });

        // 7. Quản lý Banner
        cardBanners.setOnClickListener(v -> {
             startActivity(new Intent(this, AdminBannerActivity.class));
        });

        // 8. Quản lý Đánh giá
        cardReviews.setOnClickListener(v -> {
             Toast.makeText(this, "Tính năng Đánh giá đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadDataFromFirebase() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalRevenue = 0;
                int ordersToday = 0;
                
                // Mảng lưu doanh thu 7 ngày gần nhất
                // Index 0: Hôm nay, 1: Hôm qua, ... 6: 7 ngày trước
                double[] revenueLast7Days = new double[7];
                
                long currentTime = System.currentTimeMillis();
                Calendar currentCal = Calendar.getInstance();
                currentCal.setTimeInMillis(currentTime);
                int currentDay = currentCal.get(Calendar.DAY_OF_YEAR);
                int currentYear = currentCal.get(Calendar.YEAR);

                for (DataSnapshot data : snapshot.getChildren()) {
                    try {
                        Order order = data.getValue(Order.class);
                        if (order != null) {
                            // Trừ các đơn bị hủy nếu cần
                            if ("Cancelled".equalsIgnoreCase(order.getStatus())) continue;

                            // 1. Tính tổng doanh thu
                            totalRevenue += order.getTotalAmount();
                            
                            // 2. Tính đơn hôm nay
                            long orderTime = order.getOrderDate();
                            Calendar orderCal = Calendar.getInstance();
                            orderCal.setTimeInMillis(orderTime);
                            
                            if (orderCal.get(Calendar.YEAR) == currentYear && 
                                orderCal.get(Calendar.DAY_OF_YEAR) == currentDay) {
                                ordersToday++;
                            }
                            
                            // 3. Tính cho chart (7 ngày gần nhất)
                            long diffInMillis = currentTime - orderTime;
                            long daysDiff = diffInMillis / (24 * 60 * 60 * 1000);
                            
                            if (daysDiff >= 0 && daysDiff < 7) {
                                revenueLast7Days[(int) daysDiff] += order.getTotalAmount();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                // Update UI Texts
                tvTotalRevenue.setText(decimalFormat.format(totalRevenue));
                tvOrdersToday.setText(String.valueOf(ordersToday));
                
                // Update Chart
                setupBarChart(revenueLast7Days);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminDashboardActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBarChart(double[] revenueData) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        
        // Duyệt ngược từ 6 về 0 (để hiển thị từ quá khứ đến hiện tại)
        for (int i = 6; i >= 0; i--) {
            // Tạo label ngày
            Calendar tempCal = (Calendar) cal.clone();
            tempCal.add(Calendar.DAY_OF_YEAR, -i);
            labels.add(sdf.format(tempCal.getTime()));
            
            // Add entry. Trục X là index 0->6
            entries.add(new BarEntry(6 - i, (float) revenueData[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu (VNĐ)");
        dataSet.setColor(Color.parseColor("#4CAF50")); // Xanh lá
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);
        barChart.getAxisRight().setEnabled(false);
        
        barChart.animateY(1000);
        barChart.invalidate();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Already on dashboard - do nothing
        } else if (id == R.id.nav_product) {
            startActivity(new Intent(AdminDashboardActivity.this, AdminProductListActivity.class));
        } else if (id == R.id.nav_category) {
            startActivity(new Intent(AdminDashboardActivity.this, AdminCategoryListActivity.class));
        } else if (id == R.id.nav_order) {
            startActivity(new Intent(AdminDashboardActivity.this, AdminOrdersActivity.class));
        } else if (id == R.id.nav_users) {
            startActivity(new Intent(AdminDashboardActivity.this, ManageUsersActivity.class));
        } else if (id == R.id.nav_review) {
            // Review page not implemented yet
            Toast.makeText(AdminDashboardActivity.this, "Chưa có trang Review", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_banner) {
            startActivity(new Intent(AdminDashboardActivity.this, AdminBannerActivity.class));
        } else if (id == R.id.nav_revenue) {
            startActivity(new Intent(AdminDashboardActivity.this, RevenueReportActivity.class));
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            // Correct LoginActivity package
            startActivity(new Intent(AdminDashboardActivity.this, com.example.myapplication.UI.Login.LoginActivity.class));
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
