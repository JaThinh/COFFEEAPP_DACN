package com.example.myapplication.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.UI.product.OrderDetailActivity;
import com.example.myapplication.model.Bill; // Thay Order bằng Bill
import com.example.myapplication.UI.profile.OrderHistoryActivity;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Bill> orderList; // Thay Order bằng Bill
    private OrderHistoryActivity context;

    public OrderAdapter(List<Bill> orderList, OrderHistoryActivity context) {
        this.orderList = orderList;
        this.context = context;
    }

    public void setOrders(List<Bill> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Bill bill = orderList.get(position);

        holder.textOrderId.setText("Mã đơn: " + bill.getBillId());
        holder.textOrderDate.setText("Thời gian: " + bill.getTimestamp());
        holder.textOrderTotal.setText("Tổng: " + String.format("%.2f", bill.getTotal()) + " VNĐ");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderId", bill.getBillId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textOrderId, textOrderDate, textOrderTotal;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textOrderId = itemView.findViewById(R.id.text_order_id);
            textOrderDate = itemView.findViewById(R.id.text_order_date);
            textOrderTotal = itemView.findViewById(R.id.text_order_total);
        }
    }
}