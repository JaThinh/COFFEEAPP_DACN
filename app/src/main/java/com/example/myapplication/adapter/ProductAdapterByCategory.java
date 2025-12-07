package com.example.myapplication.adapter;

import android.content.Context;
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

public class ProductAdapterByCategory extends RecyclerView.Adapter<ProductAdapterByCategory.ProductViewHolder> {

    private List<Product> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product, ImageView sharedImageView);
    }

    public ProductAdapterByCategory(List<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        if (product == null) return;
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public void updateList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.iv_product_image);
            productName = itemView.findViewById(R.id.tv_product_name);
            productPrice = itemView.findViewById(R.id.tv_product_price);
        }

        public void bind(final Product product, final OnProductClickListener listener) {
            productName.setText(product.getName());

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            productPrice.setText(formatter.format(product.getPrice()));

            Glide.with(itemView.getContext())
                    .load(product.getImageUrl())
                    .into(productImage);

            itemView.setOnClickListener(v -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onProductClick(product, productImage);
                }
            });
        }
    }
}
