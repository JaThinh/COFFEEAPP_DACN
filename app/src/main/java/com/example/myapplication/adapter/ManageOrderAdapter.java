package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Admin.AdminOrderDetailActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.FirebaseOrder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManageOrderAdapter extends RecyclerView.Adapter<ManageOrderAdapter.ManageOrderViewHolder> {

    private List<FirebaseOrder> orderList;
    private Context context;

    public ManageOrderAdapter(Context context, List<FirebaseOrder> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ManageOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_order, parent, false);
        return new ManageOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageOrderViewHolder holder, int position) {
        FirebaseOrder order = orderList.get(position);

        holder.tvOrderId.setText("Order ID: " + order.getOrderId());
        holder.tvOrderStatus.setText("Status: " + order.getStatus());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvOrderTimestamp.setText(sdf.format(new Date(order.getTimestamp())));

        holder.btnUpdateStatus.setOnClickListener(v -> {
            showStatusMenu(v, order.getOrderId());
        });

        holder.btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminOrderDetailActivity.class);
            intent.putExtra("ORDER_DETAIL", new Gson().toJson(order));
            context.startActivity(intent);
        });
    }

    private void showStatusMenu(View view, String orderId) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.order_status_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            String newStatus = item.getTitle().toString();
            updateOrderStatus(orderId, newStatus);
            return true;
        });
        popupMenu.show();
    }

    private void updateOrderStatus(String orderId, String newStatus) {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders").child(orderId);
        orderRef.child("status").setValue(newStatus);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ManageOrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderStatus, tvOrderTimestamp;
        Button btnUpdateStatus, btnViewDetails;

        public ManageOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderTimestamp = itemView.findViewById(R.id.tvOrderTimestamp);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
