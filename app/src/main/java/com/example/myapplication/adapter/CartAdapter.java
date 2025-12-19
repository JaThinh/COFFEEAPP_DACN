package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemCartBinding;
import com.bumptech.glide.Glide;
import com.example.myapplication.model.CartItem;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private CartItemListener listener;

    public interface CartItemListener {
        void onIncrease(CartItem item);
        void onDecrease(CartItem item);
        void onRemove(CartItem item);
    }

    public CartAdapter(List<CartItem> cartItems, CartItemListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartBinding binding = ItemCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem, listener);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateCartItems(List<CartItem> newItems) {
        this.cartItems = newItems;
        notifyDataSetChanged();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        private ItemCartBinding binding;

        public CartViewHolder(ItemCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final CartItem cartItem, final CartItemListener listener) {
            if (cartItem == null) return;

            binding.tvCartItemName.setText(cartItem.getProductName());
            binding.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));

            String imgUrl = cartItem.getImageUrl();
            if (imgUrl != null && !imgUrl.isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(imgUrl)
                        .placeholder(R.drawable.ic_coffee)
                        .error(R.drawable.ic_coffee)
                        .into(binding.ivCartItemImage);
            } else if (cartItem.getImageResId() != 0) {
                binding.ivCartItemImage.setImageResource(cartItem.getImageResId());
            } else {
                binding.ivCartItemImage.setImageResource(R.drawable.ic_coffee);
            }

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            binding.tvCartItemPrice.setText(formatter.format(cartItem.getProductPrice()));

            StringBuilder optionsText = new StringBuilder();
            if (cartItem.getSize() != null && !cartItem.getSize().isEmpty()) {
                optionsText.append("Size: ").append(cartItem.getSize());
            }
            if (cartItem.getSugar() != null && !cartItem.getSugar().isEmpty()) {
                if (optionsText.length() > 0) optionsText.append(", ");
                optionsText.append("Đường: ").append(cartItem.getSugar());
            }
            if (cartItem.getIce() != null && !cartItem.getIce().isEmpty()) {
                if (optionsText.length() > 0) optionsText.append(", ");
                optionsText.append("Đá: ").append(cartItem.getIce());
            }
            binding.tvCartItemOptions.setText(optionsText.toString());

            if(listener != null) {
                binding.btnIncreaseQuantity.setOnClickListener(v -> listener.onIncrease(cartItem));
                binding.btnDecreaseQuantity.setOnClickListener(v -> listener.onDecrease(cartItem));
                binding.btnRemoveItem.setOnClickListener(v -> listener.onRemove(cartItem));
            }
        }
    }
}
