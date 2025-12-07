package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton; // Giữ nếu item_cart dùng ImageButton cho các nút +/-/remove, nếu không thì bỏ

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Item.CartItem;
import com.example.myapplication.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private final List<CartItem> cartItems;

    public OrderItemAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public OrderItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemAdapter.ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        holder.tvCartProductName.setText(cartItem.getProduct().getName());

        String imageUrl = cartItem.getProduct().getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_launcher_background);
        }

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvCartProductPrice.setText(formatter.format(cartItem.getTotalPrice()));

        holder.tvCartQuantity.setText(String.valueOf(cartItem.getQuantity()));

        holder.btnCartMinus.setVisibility(View.GONE);
        holder.btnCartPlus.setVisibility(View.GONE);
        holder.btnCartRemove.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvCartProductName, tvCartProductPrice, tvCartQuantity;

        ImageButton btnCartMinus, btnCartPlus, btnCartRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvCartProductName = itemView.findViewById(R.id.tvCartProductName);
            tvCartProductPrice = itemView.findViewById(R.id.tvCartProductPrice);
            tvCartQuantity = itemView.findViewById(R.id.tvCartQuantity);
            btnCartMinus = itemView.findViewById(R.id.btnCartMinus);
            btnCartPlus = itemView.findViewById(R.id.btnCartPlus);
            btnCartRemove = itemView.findViewById(R.id.btnCartRemove);
        }
    }
}