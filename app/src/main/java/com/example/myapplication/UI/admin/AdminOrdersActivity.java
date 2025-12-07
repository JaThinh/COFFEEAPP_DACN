package com.example.myapplication.UI.admin;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.AdminOrderAdapter;
import com.example.myapplication.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminOrdersActivity extends AppCompatActivity implements AdminOrderAdapter.OnOrderActionListener {

    private RecyclerView recyclerViewOrders;
    private AdminOrderAdapter orderAdapter;
    private List<Order> orderList;
    private DatabaseReference ordersRef;
    private ValueEventListener ordersValueEventListener;
    private boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerViewOrders = findViewById(R.id.recycler_view_admin_orders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        orderAdapter = new AdminOrderAdapter(this, orderList, this);
        recyclerViewOrders.setAdapter(orderAdapter);

        // Kết nối Firebase - đảm bảo tên nhánh là "orders" (chữ thường)
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        
        // Bắt đầu lắng nghe Real-time
        startListeningForOrders();
    }

    private void startListeningForOrders() {
        ordersValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> newOrderList = new ArrayList<>();
                boolean hasNewPendingOrder = false;

                // Duyệt qua tất cả User ID
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userIdKey = userSnapshot.getKey(); // Lấy User ID từ key của node cha

                    // Duyệt qua tất cả Order của User đó
                    for (DataSnapshot orderSnapshot : userSnapshot.getChildren()) {
                        try {
                            Order order = orderSnapshot.getValue(Order.class);
                            if (order != null) {
                                // FIX LỖI: Gán userId và orderId từ Key nếu trong Object bị thiếu
                                if (order.getUserId() == null || order.getUserId().isEmpty()) {
                                    order.setUserId(userIdKey);
                                }
                                if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
                                    order.setOrderId(orderSnapshot.getKey());
                                }
                                
                                // Kiểm tra trạng thái Pending để thông báo
                                if (!isFirstLoad && "Pending".equalsIgnoreCase(order.getStatus())) {
                                    hasNewPendingOrder = true;
                                }
                                
                                newOrderList.add(order);
                            }
                        } catch (Exception e) {
                            Log.e("AdminOrders", "Lỗi parse Order: " + e.getMessage());
                        }
                    }
                }

                // Sắp xếp đơn mới nhất lên đầu
                Collections.sort(newOrderList, (o1, o2) -> Long.compare(o2.getOrderDate(), o1.getOrderDate()));

                orderList.clear();
                orderList.addAll(newOrderList);
                orderAdapter.notifyDataSetChanged();

                // Nếu có đơn mới (không phải lần load đầu tiên), phát âm thanh
                if (!isFirstLoad && hasNewPendingOrder) {
                    playNotificationSound();
                    Toast.makeText(AdminOrdersActivity.this, "Có đơn hàng mới!", Toast.LENGTH_LONG).show();
                }
                
                isFirstLoad = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminOrdersActivity.this, "Lỗi tải đơn hàng: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        ordersRef.addValueEventListener(ordersValueEventListener);
    }

    private void playNotificationSound() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ordersRef != null && ordersValueEventListener != null) {
            ordersRef.removeEventListener(ordersValueEventListener);
        }
    }

    @Override
    public void onConfirmOrder(Order order) {
        // Cập nhật trạng thái thành "Confirmed"
        updateOrderStatus(order, "Confirmed");
    }

    @Override
    public void onCancelOrder(Order order) {
        // Cập nhật trạng thái thành "Cancelled"
        updateOrderStatus(order, "Cancelled");
    }

    private void updateOrderStatus(Order order, String newStatus) {
        if (order.getUserId() == null || order.getOrderId() == null) {
             Log.e("AdminOrders", "UserID hoặc OrderID bị null. Không thể update.");
             Toast.makeText(this, "Lỗi dữ liệu đơn hàng (ID null), không thể cập nhật", Toast.LENGTH_SHORT).show();
             return;
        }
        
        Log.d("AdminOrders", "Updating order: users/" + order.getUserId() + "/" + order.getOrderId() + " -> " + newStatus);

        DatabaseReference specificOrderRef = ordersRef
                .child(order.getUserId())
                .child(order.getOrderId());

        specificOrderRef.child("status").setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã cập nhật trạng thái: " + newStatus, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminOrders", "Update failed: " + e.getMessage());
                    Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
