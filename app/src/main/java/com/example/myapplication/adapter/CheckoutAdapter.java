package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemCheckoutSummaryBinding;
import com.example.myapplication.model.CartItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder> {

    private final Context context;
    private final List<CartItem> cartItems;

    public CheckoutAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCheckoutSummaryBinding binding = ItemCheckoutSummaryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CheckoutViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    class CheckoutViewHolder extends RecyclerView.ViewHolder {
        private final ItemCheckoutSummaryBinding binding;

        CheckoutViewHolder(@NonNull ItemCheckoutSummaryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CartItem cartItem) {
            if (cartItem == null) return;

            // Sử dụng đúng ID từ item_checkout_summary.xml
            binding.tvItemName.setText(cartItem.getProductName());
            binding.tvItemQuantity.setText("x" + cartItem.getQuantity());

            double totalPriceForItem = cartItem.getProductPrice() * cartItem.getQuantity();
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            binding.tvItemPrice.setText(formatter.format(totalPriceForItem));
        }
    }
}
