package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.UI.Detail.ProductDetailActivity;
import com.example.myapplication.model.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

// SỬA LỖI: Xóa phương thức updateProductList bị lỗi
public class ProductHomeAdapter extends RecyclerView.Adapter<ProductHomeAdapter.ProductViewHolder> {

    private final Context context;
    private List<Product> productList;

    public ProductHomeAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_home, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        if (product == null) return;

        holder.tvProductName.setText(product.getName());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvProductPrice.setText(formatter.format(product.getPrice()));

        holder.tvProductDescription.setText(product.getDescription());

        String imageUrl = product.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.coffee_image)
                    .into(holder.imgProduct);
        } else {
            Glide.with(context)
                    .load(R.drawable.coffee_image)
                    .into(holder.imgProduct);
        }

        holder.itemView.setOnClickListener(v -> {
            if (product.getId() == null || product.getId().isEmpty()) {
                return;
            }
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvProductPrice, tvProductDescription;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvProductDescription = itemView.findViewById(R.id.tv_product_description);
        }
    }
}
