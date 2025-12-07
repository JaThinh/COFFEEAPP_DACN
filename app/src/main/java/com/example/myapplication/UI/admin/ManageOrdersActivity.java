package com.example.myapplication.UI.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.adapter.AdminOrderAdapter;
import com.example.myapplication.databinding.ActivityManageOrdersBinding;
import com.example.myapplication.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageOrdersActivity extends AppCompatActivity implements AdminOrderAdapter.OnOrderActionListener {

    private ActivityManageOrdersBinding binding;
    private AdminOrderAdapter adapter;
    private List<Order> orderList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản Lý Đơn Hàng");
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        setupRecyclerView();
        loadOrders();
    }

    private void setupRecyclerView() {
        orderList = new ArrayList<>();
        // FIXED: Truyền 'this' làm tham số thứ 3 để lắng nghe sự kiện
        adapter = new AdminOrderAdapter(this, orderList, this);
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrders.setAdapter(adapter);
    }

    private void loadOrders() {
        binding.progressBar.setVisibility(View.VISIBLE);
        
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                
                // Cấu trúc Firebase: orders -> userId -> orderId -> OrderObject
                for (DataSnapshot userOrdersSnapshot : snapshot.getChildren()) {
                    // userOrdersSnapshot là node của từng userId
                    for (DataSnapshot orderSnapshot : userOrdersSnapshot.getChildren()) {
                        // orderSnapshot là từng đơn hàng cụ thể
                        try {
                            Order order = orderSnapshot.getValue(Order.class);
                            if (order != null) {
                                // Nếu orderId chưa có (do code cũ), lấy từ key
                                if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
                                    order.setOrderId(orderSnapshot.getKey());
                                }
                                orderList.add(order);
                            }
                        } catch (Exception e) {
                            // Bỏ qua nếu lỗi parse data
                        }
                    }
                }
                
                // Sắp xếp đơn hàng mới nhất lên đầu
                // Sử dụng getTimestamp() hoặc getOrderDate() tùy vào model của bạn.
                // Ở đây dùng getOrderDate() vì trong model Order (bạn cung cấp) có field này.
                Collections.sort(orderList, (o1, o2) -> Long.compare(o2.getOrderDate(), o1.getOrderDate()));
                
                adapter.notifyDataSetChanged();
                binding.progressBar.setVisibility(View.GONE);
                
                if (orderList.isEmpty()) {
                    Toast.makeText(ManageOrdersActivity.this, "Chưa có đơn hàng nào", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(ManageOrdersActivity.this, "Lỗi tải đơn hàng: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConfirmOrder(Order order) {
        updateOrderStatus(order, "Confirmed");
    }

    @Override
    public void onCancelOrder(Order order) {
        updateOrderStatus(order, "Cancelled");
    }

    private void updateOrderStatus(Order order, String newStatus) {
        if (order.getUserId() == null || order.getOrderId() == null) {
            Toast.makeText(this, "Thông tin đơn hàng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Đường dẫn: orders -> userId -> orderId -> status
        ordersRef.child(order.getUserId())
                .child(order.getOrderId())
                .child("status")
                .setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ManageOrdersActivity.this, "Đã cập nhật trạng thái: " + newStatus, Toast.LENGTH_SHORT).show();
                    // Không cần reload thủ công vì addValueEventListener sẽ tự bắt sự kiện thay đổi
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ManageOrdersActivity.this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
