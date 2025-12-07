package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // <-- Thêm import này
import com.example.myapplication.R;
import com.example.myapplication.manager.CartManager;
import com.example.myapplication.Item.CartItem; // Đảm bảo đường dẫn này đúng
import java.text.DecimalFormat;
import java.text.NumberFormat; // Thêm để định dạng tiền tệ
import java.util.List;
import java.util.Locale; // Thêm để định dạng tiền tệ

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final Context context;
    private List<CartItem> cartItems;
    private final CartManager cartManager;
    private OnCartItemChangeListener listener;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        this.cartManager = CartManager.getInstance();
    }

    public interface OnCartItemChangeListener {
        void onCartItemQuantityChanged();
        void onCartItemRemoved();
    }

    public void setOnCartItemChangeListener(OnCartItemChangeListener listener) {
        this.listener = listener;
    }

    public void updateCartItems(List<CartItem> newCartItems) {
        this.cartItems = newCartItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.tvCartProductName.setText(cartItem.getProduct().getName());

        //Sử dụng Glide để tải ảnh từ URL
        String imageUrl = cartItem.getProduct().getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.imgProduct);
        } else {

            holder.imgProduct.setImageResource(R.drawable.ic_launcher_background);
        }

        // Thay đổi định dạng tiền tệ để linh hoạt hơn
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvCartProductPrice.setText(formatter.format(cartItem.getTotalPrice()));

        holder.tvCartQuantity.setText(String.valueOf(cartItem.getQuantity()));

        holder.btnCartMinus.setOnClickListener(v -> {
            cartManager.removeProduct(cartItem.getProduct());
            updateCartItems(cartManager.getCartItems());
            if (listener != null) listener.onCartItemQuantityChanged();
        });

        holder.btnCartPlus.setOnClickListener(v -> {
            cartManager.addProduct(cartItem.getProduct());
            updateCartItems(cartManager.getCartItems());
            if (listener != null) listener.onCartItemQuantityChanged();
        });

        holder.btnCartRemove.setOnClickListener(v -> {
            cartManager.removeAllProduct(cartItem.getProduct());
            updateCartItems(cartManager.getCartItems());
            if (listener != null) listener.onCartItemRemoved();
        });
    }

    @Override
    public int getItemCount() {
        return (cartItems != null) ? cartItems.size() : 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvCartProductName;
        TextView tvCartProductPrice;
        TextView tvCartQuantity;
        ImageButton btnCartMinus;
        ImageButton btnCartPlus;
        ImageButton btnCartRemove;

        public CartViewHolder(@NonNull View itemView) {
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