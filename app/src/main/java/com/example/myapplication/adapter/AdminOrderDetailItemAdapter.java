package com.example.myapplication.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // REVERTED
import com.example.myapplication.R;
import com.example.myapplication.model.CartItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminOrderDetailItemAdapter extends RecyclerView.Adapter<AdminOrderDetailItemAdapter.ViewHolder> {

    private final List<CartItem> cartItems;

    public AdminOrderDetailItemAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductOptions, tvQuantity, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductOptions = itemView.findViewById(R.id.tv_product_options);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
        }

        public void bind(CartItem item) {
            tvProductName.setText(item.getProductName());
            tvQuantity.setText("Số lượng: " + item.getQuantity());
            tvPrice.setText(formatCurrency(item.getProductPrice() * item.getQuantity()));

            List<String> options = new ArrayList<>();
            if (!TextUtils.isEmpty(item.getSize())) {
                options.add("Size: " + item.getSize());
            }
            if (!TextUtils.isEmpty(item.getSugar())) {
                options.add(item.getSugar());
            }
            if (!TextUtils.isEmpty(item.getIce())) {
                options.add(item.getIce());
            }
            if (options.isEmpty()) {
                tvProductOptions.setVisibility(View.GONE);
            } else {
                tvProductOptions.setVisibility(View.VISIBLE);
                tvProductOptions.setText(TextUtils.join(", ", options));
            }

            String imageUrl = item.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(itemView.getContext()) // REVERTED
                        .load(imageUrl)
                        .placeholder(R.drawable.bg_placeholder) 
                        .error(R.drawable.bg_placeholder)
                        .into(ivProductImage);
            } else {
                ivProductImage.setImageResource(R.drawable.bg_placeholder);
            }
        }

        private String formatCurrency(double price) {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            return formatter.format(price);
        }
    }
}
