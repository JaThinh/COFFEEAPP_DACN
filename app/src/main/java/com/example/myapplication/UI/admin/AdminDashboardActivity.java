package com.example.myapplication.UI.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.UI.Login.LoginActivity;
import com.example.myapplication.adapter.TopSellingAdapter;
import com.example.myapplication.manager.CartManager;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.Order;
import com.example.myapplication.model.Product;
import com.example.myapplication.util.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminDashboardActivity extends AppCompatActivity {

    // Views
    private TextView tvRevenueToday, tvOrdersCount, tvRevenueMonth;
    private TextView btnLogout;
    private TextView tvNoTopSelling;

    // Stats Cards
    private CardView cardRevenueToday, cardOrdersNew, cardRevenueMonth;

    // Menu Cards
    private CardView cvProducts, cvOrders, cvCategories, cvUsers, cvBanners;

    // Recycler View
    private RecyclerView rvTopSelling;
    private TopSellingAdapter topSellingAdapter;
    private List<Product> topSellingList;

    // Firebase
    private DatabaseReference mDatabase;
    private ValueEventListener statsListener;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        initEvents();
        loadStatistics();
        
        runLayoutAnimation();
    }

    private void initViews() {
        // Text Views
        tvRevenueToday = findViewById(R.id.tv_revenue_today);
        tvOrdersCount = findViewById(R.id.tv_orders_count);
        tvRevenueMonth = findViewById(R.id.tv_revenue_month);
        btnLogout = findViewById(R.id.btn_logout);
        tvNoTopSelling = findViewById(R.id.tv_no_top_selling);

        // Stats Cards
        cardRevenueToday = findViewById(R.id.card_revenue_today);
        cardOrdersNew = findViewById(R.id.card_orders_new);
        cardRevenueMonth = findViewById(R.id.card_revenue_month);

        // Menu Cards from Layout
        cvProducts = findViewById(R.id.cv_manage_products);
        cvOrders = findViewById(R.id.cv_manage_orders);
        cvCategories = findViewById(R.id.cv_manage_categories);
        cvUsers = findViewById(R.id.cv_manage_users);
        cvBanners = findViewById(R.id.cv_manage_banners);
        
        // RecyclerView
        rvTopSelling = findViewById(R.id.rv_top_selling);
    }

    private void setupRecyclerView() {
        topSellingList = new ArrayList<>();
        topSellingAdapter = new TopSellingAdapter(topSellingList);
        rvTopSelling.setLayoutManager(new LinearLayoutManager(this));
        rvTopSelling.setAdapter(topSellingAdapter);
    }

    private void initEvents() {
        cvProducts.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, ManageProductsActivity.class)));
        
        // CẬP NHẬT: Chuyển đến ManageCategoryActivity mới
        cvCategories.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, ManageCategoryActivity.class)));
        
        cvOrders.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, AdminOrdersActivity.class)));
        cvUsers.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, ManageUsersActivity.class)));
        cvBanners.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, ManageBannersActivity.class)));

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            sessionManager.logoutUser();
            CartManager.getInstance().clearCartOnLogout();
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        
        cardOrdersNew.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, AdminOrdersActivity.class)));
    }

    private void loadStatistics() {
        if (statsListener != null) {
            mDatabase.child("orders").removeEventListener(statsListener);
        }

        statsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double todayRevenue = 0;
                double monthRevenue = 0;
                int newOrders = 0;
                
                Map<String, Product> productSalesMap = new HashMap<>();

                Calendar calendar = Calendar.getInstance();
                
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                long startOfToday = calendar.getTimeInMillis();

                calendar.set(Calendar.DAY_OF_MONTH, 1);
                long startOfMonth = calendar.getTimeInMillis();

                for (DataSnapshot userOrders : snapshot.getChildren()) {
                    for (DataSnapshot orderSnapshot : userOrders.getChildren()) {
                        try {
                            Map<String, Object> orderMap = (Map<String, Object>) orderSnapshot.getValue();
                            if (orderMap == null) continue;

                            double orderPrice = parseDouble(orderMap.get("totalPrice"));
                            long orderTime = parseLong(orderMap.get("orderDate"));
                            String status = String.valueOf(orderMap.get("status"));
                            
                            if ("Pending".equalsIgnoreCase(status)) { 
                                newOrders++;
                            }
                            
                            if (!"Cancelled".equalsIgnoreCase(status) && !"Đã hủy".equalsIgnoreCase(status)) {
                                if (orderTime >= startOfToday) {
                                    todayRevenue += orderPrice;
                                }
                                if (orderTime >= startOfMonth) {
                                    monthRevenue += orderPrice;
                                }
                                
                                Order order = orderSnapshot.getValue(Order.class);
                                if (order != null && order.getCartItems() != null) {
                                    updateProductSales(productSalesMap, order.getCartItems());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                updateStatsUI(todayRevenue, monthRevenue, newOrders);
                updateTopSellingList(productSalesMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminDashboardActivity.this, "Lỗi tải thống kê: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        
        mDatabase.child("orders").addValueEventListener(statsListener);
    }
    
    private void updateProductSales(Map<String, Product> productSalesMap, List<CartItem> cartItems) {
        for (CartItem item : cartItems) {
            String productName = item.getName() != null ? item.getName() : item.getProductName();
            if (productName == null) continue;

            int quantity = item.getQuantity();
            
            Product p = productSalesMap.get(productName);
            if (p != null) {
                p.setSoldCount(p.getSoldCount() + quantity);
            } else {
                p = new Product();
                p.setName(productName);
                p.setImageUrl(item.getImageUrl());
                double uPrice = item.getUnitPrice() > 0 ? item.getUnitPrice() : item.getProductPrice();
                p.setPrice(uPrice);
                p.setSoldCount(quantity);
                productSalesMap.put(productName, p);
            }
        }
    }
    
    private double parseDouble(Object obj) {
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        try {
            return Double.parseDouble(String.valueOf(obj));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private long parseLong(Object obj) {
        if (obj instanceof Number) return ((Number) obj).longValue();
        try {
            return Long.parseLong(String.valueOf(obj));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void updateStatsUI(double today, double month, int orders) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        
        if (tvRevenueToday != null) tvRevenueToday.setText(formatter.format(today));
        if (tvRevenueMonth != null) tvRevenueMonth.setText(formatter.format(month));
        if (tvOrdersCount != null) tvOrdersCount.setText(String.valueOf(orders));
    }

    private void updateTopSellingList(Map<String, Product> productSalesMap) {
        topSellingList.clear();
        topSellingList.addAll(productSalesMap.values());
        Collections.sort(topSellingList, (p1, p2) -> Integer.compare(p2.getSoldCount(), p1.getSoldCount()));

        if (topSellingList.size() > 5) {
            topSellingList = new ArrayList<>(topSellingList.subList(0, 5));
        }

        topSellingAdapter.updateList(topSellingList);

        boolean isEmpty = topSellingList.isEmpty();
        tvNoTopSelling.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rvTopSelling.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
    
    private void runLayoutAnimation() {
        // Animation logic
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (statsListener != null) {
            mDatabase.child("orders").removeEventListener(statsListener);
        }
    }
}