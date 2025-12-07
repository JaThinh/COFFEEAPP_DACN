package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemTopSellingProductBinding;
import com.example.myapplication.model.Product;

import java.util.List;

public class TopSellingProductAdapter extends RecyclerView.Adapter<TopSellingProductAdapter.ViewHolder> {

    private final List<Product> productList;

    public TopSellingProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTopSellingProductBinding binding = ItemTopSellingProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemTopSellingProductBinding binding;

        public ViewHolder(ItemTopSellingProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Product product) {
            int rank = getAdapterPosition() + 1;
            if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                return; // Do not bind if position is not valid
            }
            binding.tvRank.setText(String.valueOf(rank));
            binding.tvProductName.setText(product.getName());
            binding.tvCategory.setText(product.getCategoryName());
            binding.tvQuantitySold.setText(String.format("%d đã bán", product.getSoldCount()));

            String imageUrl = product.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.bg_placeholder)
                        .error(R.drawable.bg_placeholder)
                        .into(binding.ivProductImage);
            } else {
                binding.ivProductImage.setImageResource(R.drawable.bg_placeholder);
            }
        }
    }
}
