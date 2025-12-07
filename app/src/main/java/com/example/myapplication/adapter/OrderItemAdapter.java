package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.CartItem; // THÊM IMPORT

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private final List<CartItem> cartItems;

    public OrderItemAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail_product, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        // SỬA LỖI: Sử dụng các phương thức getter từ model.CartItem
        holder.tvProductName.setText(cartItem.getProductName());
        holder.tvQuantity.setText("x" + cartItem.getQuantity());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(formatter.format(cartItem.getProductPrice()));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvQuantity, tvPrice;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
        }
    }
}