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
import com.example.myapplication.R;
import com.example.myapplication.model.CartProduct;
import com.google.android.material.button.MaterialButton;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.CartProductViewHolder> {

    private List<CartProduct> cartItems;
    private Context context;
    private CartItemListener listener;

    public interface CartItemListener {
        void onQuantityChanged();
        void onItemRemoved(int position);
    }

    public CartProductAdapter(List<CartProduct> cartItems, Context context, CartItemListener listener) {
        this.cartItems = cartItems;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_product, parent, false);
        return new CartProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartProductViewHolder holder, int position) {
        CartProduct item = cartItems.get(position);

        if (item.getImageResId() != 0) {
            holder.productImage.setImageResource(item.getImageResId());
        } else {
            holder.productImage.setImageResource(R.drawable.ic_coffee);
        }
        holder.productName.setText(item.getName());
        holder.productOptions.setText(item.getOptions());
        holder.quantity.setText(String.valueOf(item.getQuantity()));

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.productPrice.setText(currencyFormat.format(item.getPrice()));

        holder.plusButton.setOnClickListener(v -> {
            int currentQuantity = item.getQuantity();
            item.setQuantity(currentQuantity + 1);
            holder.quantity.setText(String.valueOf(item.getQuantity()));
            if (listener != null) {
                listener.onQuantityChanged();
            }
        });

        holder.minusButton.setOnClickListener(v -> {
            int currentQuantity = item.getQuantity();
            if (currentQuantity > 1) {
                item.setQuantity(currentQuantity - 1);
                holder.quantity.setText(String.valueOf(item.getQuantity()));
                if (listener != null) {
                    listener.onQuantityChanged();
                }
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                // We get the adapter position to safely remove the item
                listener.onItemRemoved(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productOptions, quantity, productPrice;
        MaterialButton minusButton, plusButton;
        ImageButton deleteButton;

        public CartProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.iv_product_image);
            productName = itemView.findViewById(R.id.tv_product_name);
            productOptions = itemView.findViewById(R.id.tv_product_options);
            quantity = itemView.findViewById(R.id.tv_quantity);
            productPrice = itemView.findViewById(R.id.tv_product_price);
            minusButton = itemView.findViewById(R.id.btn_minus);
            plusButton = itemView.findViewById(R.id.btn_plus);
            deleteButton = itemView.findViewById(R.id.btn_delete_item);
        }
    }
}