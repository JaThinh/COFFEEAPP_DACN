package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.CartItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CheckoutSummaryAdapter extends RecyclerView.Adapter<CheckoutSummaryAdapter.ViewHolder> {

    private final List<CartItem> cartItems;

    public CheckoutSummaryAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout_summary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity, itemPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.tv_item_name);
            itemQuantity = itemView.findViewById(R.id.tv_item_quantity);
            itemPrice = itemView.findViewById(R.id.tv_item_price);
        }

        void bind(CartItem cartItem) {
            itemName.setText(cartItem.getProductName());
            itemQuantity.setText("x" + cartItem.getQuantity());

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            double totalPrice = cartItem.getProductPrice() * cartItem.getQuantity();
            itemPrice.setText(currencyFormat.format(totalPrice));
        }
    }
}
