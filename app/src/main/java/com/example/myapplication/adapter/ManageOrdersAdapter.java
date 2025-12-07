package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Admin.AdminOrderDetailActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.FirebaseOrder;
import com.example.myapplication.model.Notification;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManageOrdersAdapter extends RecyclerView.Adapter<ManageOrdersAdapter.OrderViewHolder> {

    private final Context context;
    private final List<FirebaseOrder> orderList;

    public ManageOrdersAdapter(Context context, List<FirebaseOrder> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        FirebaseOrder order = orderList.get(position);

        holder.tvOrderId.setText("Đơn hàng #" + order.getOrderId().substring(order.getOrderId().length() - 5));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvOrderTimestamp.setText(dateFormat.format(new Date(order.getTimestamp())));

        holder.tvOrderStatus.setText("Trạng thái: " + order.getStatus());

        holder.itemView.findViewById(R.id.btnViewDetails).setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminOrderDetailActivity.class);
            intent.putExtra("ORDER_DETAIL", new Gson().toJson(order));
            context.startActivity(intent);
        });

        // SỬA LỖI: Khôi phục lại chức năng cho nút "Hành động"
        holder.itemView.findViewById(R.id.btnUpdateStatus).setOnClickListener(v -> {
            showStatusMenu(v, order);
        });
    }

    private void showStatusMenu(View view, FirebaseOrder order) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        // Giả định bạn đã có file menu tên là order_status_menu.xml trong res/menu
        popupMenu.getMenuInflater().inflate(R.menu.order_status_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            String newStatus = item.getTitle().toString();
            updateOrderStatusAndNotify(order, newStatus);
            return true;
        });
        popupMenu.show();
    }

    private void updateOrderStatusAndNotify(FirebaseOrder order, String newStatus) {
        DatabaseReference orderRef = FirebaseDatabase.getInstance()
                .getReference("orders")
                .child(order.getOrderId());

        orderRef.child("status").setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
                    // Không cần cập nhật local `order` và `notifyDataSetChanged()`
                    // vì ChildEventListener trong Activity sẽ tự động làm điều đó.
                    sendNotificationToUser(order, newStatus);
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show());
    }

    private void sendNotificationToUser(FirebaseOrder order, String newStatus) {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance()
                .getReference("notifications")
                .child(order.getUserId())
                .push();

        String title = "Cập nhật đơn hàng #" + order.getOrderId().substring(order.getOrderId().length() - 5);
        String message = "Đơn hàng của bạn đã được cập nhật sang trạng thái: " + newStatus;

        Notification notification = new Notification(title, message, System.currentTimeMillis());

        notificationRef.setValue(notification);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderStatus, tvOrderTimestamp;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderTimestamp = itemView.findViewById(R.id.tvOrderTimestamp);
        }
    }
}
