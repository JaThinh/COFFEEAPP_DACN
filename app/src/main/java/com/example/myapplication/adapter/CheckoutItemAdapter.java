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
import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.CartItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CheckoutItemAdapter extends RecyclerView.Adapter<CheckoutItemAdapter.CheckoutViewHolder> {

    private final List<CartItem> cartItems;

    public CheckoutItemAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout, parent, false);
        return new CheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        if (cartItem == null) return;
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    static class CheckoutViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvOptions, tvQuantity, tvPrice;

        public CheckoutViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvOptions = itemView.findViewById(R.id.tvOptions);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }

        public void bind(CartItem cartItem) {
            tvProductName.setText(cartItem.getProductName());
            tvQuantity.setText("Số lượng: " + cartItem.getQuantity());

            StringBuilder options = new StringBuilder();
            if (!TextUtils.isEmpty(cartItem.getSize())) {
                options.append(cartItem.getSize());
            }
            if (!TextUtils.isEmpty(cartItem.getSugar())) {
                if (options.length() > 0) options.append(", ");
                options.append("Đường: ").append(cartItem.getSugar());
            }
            if (!TextUtils.isEmpty(cartItem.getIce())) {
                if (options.length() > 0) options.append(", ");
                options.append("Đá: ").append(cartItem.getIce());
            }

            tvOptions.setText(options.toString());

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            tvPrice.setText(formatter.format(cartItem.getProductPrice() * cartItem.getQuantity()));

            String imageUrl = cartItem.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.bg_placeholder)
                        .error(R.drawable.bg_placeholder)
                        .into(ivProductImage);
            } else {
                ivProductImage.setImageResource(R.drawable.bg_placeholder);
            }
        }
    }
}
