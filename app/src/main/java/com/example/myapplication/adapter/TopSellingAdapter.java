package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TopSellingAdapter extends RecyclerView.Adapter<TopSellingAdapter.ViewHolder> {

    private List<Product> productList;

    public TopSellingAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_selling, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvName.setText(product.getName());
        holder.tvSold.setText("Đã bán: " + product.getSoldCount());
        
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(formatter.format(product.getPrice()));

        // SỬA LỖI GLIDE
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_coffee_placeholder)
                    .error(R.drawable.ic_coffee_placeholder)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_coffee_placeholder);
        }
        
        holder.tvRank.setText("#" + (position + 1));
        if (position == 0) holder.tvRank.setTextColor(0xFFFFD700); // Gold
        else if (position == 1) holder.tvRank.setTextColor(0xFFC0C0C0); // Silver
        else if (position == 2) holder.tvRank.setTextColor(0xFFCD7F32); // Bronze
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    /**
     * Cập nhật danh sách sản phẩm và thông báo cho adapter.
     * @param newList Danh sách sản phẩm mới.
     */
    public void updateList(List<Product> newList) {
        this.productList.clear();
        this.productList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvSold, tvPrice, tvRank;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvSold = itemView.findViewById(R.id.tv_sold_count);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            tvRank = itemView.findViewById(R.id.tv_rank);
        }
    }
}