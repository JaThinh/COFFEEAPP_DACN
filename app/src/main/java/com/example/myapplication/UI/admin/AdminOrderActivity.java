package com.example.myapplication.UI.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Order;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminOrderActivity extends AppCompatActivity {

    private RecyclerView rcvOrders;
    private AdminOrderAdapter adapter;
    private List<Order> allOrders = new ArrayList<>();
    private List<Order> filteredOrders = new ArrayList<>();
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Tất cả"));
        tabLayout.addTab(tabLayout.newTab().setText("Chờ xác nhận")); 
        tabLayout.addTab(tabLayout.newTab().setText("Đang làm")); 
        tabLayout.addTab(tabLayout.newTab().setText("Hoàn thành")); 
        tabLayout.addTab(tabLayout.newTab().setText("Đã hủy")); 

        rcvOrders = findViewById(R.id.rcvOrders);
        rcvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminOrderAdapter(this, filteredOrders);
        rcvOrders.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference("orders");
        loadOrders();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterOrders(tab.getPosition());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadOrders() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allOrders.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Order order = data.getValue(Order.class);
                    if (order != null) {
                        order.setOrderId(data.getKey());
                        allOrders.add(order);
                    }
                }
                Collections.reverse(allOrders); 
                filterOrders(0); 
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void filterOrders(int tabIndex) {
        filteredOrders.clear();
        if (tabIndex == 0) {
            filteredOrders.addAll(allOrders);
        } else {
            // Giả sử status lưu: 0=Pending, 1=Processing, 2=Completed, 3=Cancelled
            int targetStatus = tabIndex - 1; 
            for (Order order : allOrders) {
                if (order.getStatus() == targetStatus) {
                    filteredOrders.add(order);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void updateOrderStatus(String orderId, int newStatus) {
        mDatabase.child(orderId).child("status").setValue(newStatus)
            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show());
    }

    // --- INNER ADAPTER ---
    public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {
        private Context context;
        private List<Order> list;

        public AdminOrderAdapter(Context context, List<Order> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_admin_order, parent, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            Order order = list.get(position);
            
            // Format ID ngắn gọn
            String displayId = order.getOrderId();
            if (displayId.length() > 6) displayId = displayId.substring(displayId.length() - 6);
            holder.tvOrderId.setText("Đơn #" + displayId);
            
            // Format ngày (Giả sử order.getDate() trả về String hoặc long)
            // Nếu là long: new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(order.getDateLong()))
            holder.tvOrderDate.setText(String.valueOf(order.getDate())); // Đơn giản hóa
            
            // Format User & Total
            holder.tvOrderUser.setText("Khách: " + (order.getName() != null ? order.getName() : "Ẩn danh"));
            
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            holder.tvOrderTotal.setText("Tổng: " + currencyFormat.format(order.getTotalPrice()));
            
            // Status
            holder.tvOrderStatus.setText(getStatusString(order.getStatus()));
            updateStatusColor(holder.tvOrderStatus, order.getStatus());
            
            holder.itemView.setOnClickListener(v -> showOrderOptions(order));
        }
        
        private String getStatusString(int status) {
            switch (status) {
                case 0: return "Chờ xác nhận";
                case 1: return "Đang làm";
                case 2: return "Hoàn thành";
                case 3: return "Đã hủy";
                default: return "Không rõ";
            }
        }
        
        private void updateStatusColor(TextView tv, int status) {
            int color;
            switch (status) {
                case 0: color = 0xFFFF9800; break; // Orange
                case 1: color = 0xFF2196F3; break; // Blue
                case 2: color = 0xFF4CAF50; break; // Green
                case 3: color = 0xFFF44336; break; // Red
                default: color = 0xFF757575; break; // Grey
            }
            tv.setTextColor(color);
        }

        private void showOrderOptions(Order order) {
            String[] options = {"Xác nhận (Đang làm)", "Hoàn thành", "Hủy đơn", "Xem chi tiết"};
            new AlertDialog.Builder(context)
                .setTitle("Thao tác đơn hàng #" + order.getOrderId().substring(order.getOrderId().length()-5))
                .setItems(options, (dialog, which) -> {
                    if (which == 0) updateOrderStatus(order.getOrderId(), 1);
                    else if (which == 1) updateOrderStatus(order.getOrderId(), 2);
                    else if (which == 2) updateOrderStatus(order.getOrderId(), 3);
                    else if (which == 3) {
                        new AlertDialog.Builder(context)
                            .setTitle("Chi tiết đơn hàng")
                            .setMessage("Địa chỉ: " + order.getAddress() + "\nSĐT: " + order.getPhone() + "\nTổng tiền: " + order.getTotalPrice())
                            .setPositiveButton("Đóng", null)
                            .show();
                    }
                }).show();
        }

        @Override
        public int getItemCount() { return list.size(); }

        class OrderViewHolder extends RecyclerView.ViewHolder {
            TextView tvOrderId, tvOrderDate, tvOrderUser, tvOrderTotal, tvOrderStatus;
            public OrderViewHolder(@NonNull View itemView) {
                super(itemView);
                tvOrderId = itemView.findViewById(R.id.tvOrderId);
                tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
                tvOrderUser = itemView.findViewById(R.id.tvOrderUser);
                tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
                tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            }
        }
    }
}
