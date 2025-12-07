package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemOrderDetailProductBinding;
import com.example.myapplication.model.CartItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class UserOrderDetailAdapter extends RecyclerView.Adapter<UserOrderDetailAdapter.ViewHolder> {

    private final List<CartItem> items;

    public UserOrderDetailAdapter(List<CartItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemOrderDetailProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<CartItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderDetailProductBinding binding;

        ViewHolder(ItemOrderDetailProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CartItem item) {
            String imageUrl = item.getImageUrl();

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                     .load(imageUrl)
                     .placeholder(R.drawable.bg_placeholder) 
                     .error(R.drawable.bg_placeholder)
                     .centerCrop()
                     .transition(DrawableTransitionOptions.withCrossFade()) // Thêm hiệu ứng mờ dần
                     .into(binding.ivProductImage);
            } else {
                binding.ivProductImage.setImageResource(R.drawable.bg_placeholder);
            }

            binding.tvProductName.setText(item.getProductName());
            binding.tvQuantity.setText("Số lượng: " + item.getQuantity());
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            binding.tvProductPrice.setText(formatter.format(item.getProductPrice()));
        }
    }
}
