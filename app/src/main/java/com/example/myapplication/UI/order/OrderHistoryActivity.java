package com.example.myapplication.UI.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.Admin.AdminOrderDetailActivity;
import com.example.myapplication.UI.Home.HomeActivity;
import com.example.myapplication.adapter.OrderHistoryAdapter;
import com.example.myapplication.databinding.ActivityOrderHistoryBinding;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.Order;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity implements OrderHistoryAdapter.OnItemClickListener {

    private ActivityOrderHistoryBinding binding;
    private OrderHistoryAdapter adapter;
    private List<Order> fullOrderList;
    private DatabaseReference ordersRef;
    private FirebaseAuth mAuth;
    
    // Listener management to prevent Permission Denied errors on logout
    private DatabaseReference userOrdersRef;
    private ValueEventListener orderListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarOrderHistory);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbarOrderHistory.setNavigationOnClickListener(v -> finish());

        mAuth = FirebaseAuth.getInstance();
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        setupRecyclerView();
        setupTabs();

        binding.btnStartShopping.setOnClickListener(v -> {
            Intent intent = new Intent(OrderHistoryActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadOrderHistory();
    }

    @Override
    protected void onStop() {
        super.onStop();
        detachListener();
    }

    private void setupRecyclerView() {
        binding.recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        fullOrderList = new ArrayList<>();
        adapter = new OrderHistoryAdapter(this, new ArrayList<>(), this);
        binding.recyclerViewOrders.setAdapter(adapter);
    }

    private void setupTabs() {
        binding.tabLayoutStatus.removeAllTabs(); // Clear existing tabs
        binding.tabLayoutStatus.addTab(binding.tabLayoutStatus.newTab().setText("Tất cả"));
        binding.tabLayoutStatus.addTab(binding.tabLayoutStatus.newTab().setText("Đang xử lý"));
        binding.tabLayoutStatus.addTab(binding.tabLayoutStatus.newTab().setText("Hoàn thành"));
        // XÓA: binding.tabLayoutStatus.addTab(binding.tabLayoutStatus.newTab().setText("Đã hủy"));

        binding.tabLayoutStatus.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab != null && tab.getText() != null) {
                    filterOrdersByStatus(tab.getText().toString());
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void filterOrdersByStatus(String statusTab) {
        List<Order> filteredList = new ArrayList<>();
        if ("Tất cả".equalsIgnoreCase(statusTab)) {
            filteredList.addAll(fullOrderList);
        } else {
            for (Order order : fullOrderList) {
                String orderStatus = order.getStatus();
                if (orderStatus == null) continue;

                boolean match = false;
                switch (statusTab) {
                    case "Đang xử lý":
                        match = "Pending".equalsIgnoreCase(orderStatus) || "Đang xử lý".equalsIgnoreCase(orderStatus);
                        break;
                    // Removed "Đang giao" case
                    case "Hoàn thành":
                        match = "Completed".equalsIgnoreCase(orderStatus) || "Hoàn thành".equalsIgnoreCase(orderStatus);
                        break;
                    // XÓA case "Đã hủy"
                }
                if (match) {
                    filteredList.add(order);
                }
            }
        }
        adapter.updateList(filteredList);
    }


    private void loadOrderHistory() {
        binding.progressBarOrders.setVisibility(View.VISIBLE);
        binding.layoutEmptyState.setVisibility(View.GONE);
        binding.recyclerViewOrders.setVisibility(View.VISIBLE);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User not logged in, can't load orders
            binding.progressBarOrders.setVisibility(View.GONE);
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
            binding.recyclerViewOrders.setVisibility(View.GONE);
            return;
        }

        String userId = currentUser.getUid();
        userOrdersRef = ordersRef.child(userId);

        if (orderListener == null) {
            orderListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    fullOrderList.clear();
                    if (snapshot.exists()) {
                        for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                            Order order = orderSnapshot.getValue(Order.class);
                            if (order != null) {
                                fullOrderList.add(order);
                            }
                        }
                        Collections.reverse(fullOrderList);

                        int selectedTabPosition = binding.tabLayoutStatus.getSelectedTabPosition();
                        if (selectedTabPosition != -1) {
                            TabLayout.Tab tab = binding.tabLayoutStatus.getTabAt(selectedTabPosition);
                            if (tab != null && tab.getText() != null) {
                                filterOrdersByStatus(tab.getText().toString());
                            }
                        } else {
                            filterOrdersByStatus("Tất cả");
                        }

                        binding.recyclerViewOrders.setVisibility(View.VISIBLE);
                        binding.layoutEmptyState.setVisibility(View.GONE);
                    } else {
                        binding.recyclerViewOrders.setVisibility(View.GONE);
                        binding.layoutEmptyState.setVisibility(View.VISIBLE);
                    }
                    binding.progressBarOrders.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Check if activity is still valid
                    if (!isFinishing() && !isDestroyed()) {
                        binding.progressBarOrders.setVisibility(View.GONE);
                        Log.e("OrderHistory", "Firebase query failed: " + error.getMessage());
                        // Don't show Toast for Permission Denied if it's due to logout
                        if (!error.getMessage().contains("Permission denied")) {
                             Toast.makeText(OrderHistoryActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            };
        }
        
        // Attach listener
        userOrdersRef.addValueEventListener(orderListener);
    }

    private void detachListener() {
        if (userOrdersRef != null && orderListener != null) {
            userOrdersRef.removeEventListener(orderListener);
        }
    }

    @Override
    public void onItemClick(Order order) {
        // Sử dụng AdminOrderDetailActivity nhưng với mode User (isAdmin = false)
        // Nếu bạn đã tạo Activity Detail riêng cho user thì thay đổi ở đây.
        // Ở đây dùng chung AdminOrderDetailActivity cho tiện
        Intent intent = new Intent(this, com.example.myapplication.UI.admin.AdminOrderDetailActivity.class);
        intent.putExtra("order", order);
        intent.putExtra("isAdmin", false);
        startActivity(intent);
    }

    @Override
    public void onCancelOrder(Order order) {
        new AlertDialog.Builder(this)
                .setTitle("Hủy đơn hàng")
                .setMessage("Bạn có chắc chắn muốn hủy đơn hàng này không?")
                .setPositiveButton("Có", (dialog, which) -> cancelOrderFirebase(order))
                .setNegativeButton("Không", null)
                .show();
    }

    private void cancelOrderFirebase(Order order) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        DatabaseReference orderRef = ordersRef.child(currentUser.getUid()).child(order.getOrderId());
        orderRef.child("status").setValue("Đã hủy")
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Đơn hàng đã được hủy thành công!", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Hủy đơn hàng thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
    
    // Đã thêm @Override để đảm bảo liên kết đúng với Interface
    @Override
    public void onConfirmCompleted(Order order) {
         new AlertDialog.Builder(this)
                .setTitle("Xác nhận hoàn thành")
                .setMessage("Bạn đã nhận được đơn hàng và muốn xác nhận hoàn thành?")
                .setPositiveButton("Xác nhận", (dialog, which) -> updateOrderStatus(order, "Completed"))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateOrderStatus(Order order, String newStatus) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        DatabaseReference orderRef = ordersRef.child(currentUser.getUid()).child(order.getOrderId());
        orderRef.child("status").setValue(newStatus)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Đã cập nhật trạng thái đơn hàng!", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    @Override
    public void onReorder(Order order) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để đặt lại đơn hàng.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (order.getCartItems() == null || order.getCartItems().isEmpty()) {
            Toast.makeText(this, "Đơn hàng này không có sản phẩm để đặt lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("carts").child(currentUser.getUid());

        for (CartItem item : order.getCartItems()) {
            String cartItemId = cartRef.push().getKey();
            if (cartItemId != null) {
                cartRef.child(cartItemId).setValue(item);
            }
        }

        Toast.makeText(this, "Đã thêm sản phẩm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
    }
}
