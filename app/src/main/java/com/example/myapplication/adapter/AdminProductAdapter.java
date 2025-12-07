package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {

    private final List<Product> productList;
    private final OnProductListener listener;

    public interface OnProductListener {
        void onEditClick(Product product);
        void onDeleteClick(Product product);
    }

    public AdminProductAdapter(List<Product> productList, OnProductListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvCategory, tvPrice;
        ImageButton btnEdit, btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvCategory = itemView.findViewById(R.id.tv_product_category);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            btnEdit = itemView.findViewById(R.id.btn_edit_product);
            btnDelete = itemView.findViewById(R.id.btn_delete_product);
        }

        void bind(final Product product, final OnProductListener listener) {
            if (product == null) return;

            tvName.setText(product.getName());
            tvCategory.setText(product.getCategoryId());

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            tvPrice.setText(formatter.format(product.getPrice()));

            // SỬA LỖI GLIDE: Kiểm tra imageUrl trước khi load
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.ic_coffee_placeholder)
                        .error(R.drawable.ic_coffee_placeholder)
                        .into(imgProduct);
            } else {
                // Nếu không có ảnh, hiển thị placeholder
                imgProduct.setImageResource(R.drawable.ic_coffee_placeholder);
            }

            btnEdit.setOnClickListener(v -> listener.onEditClick(product));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(product));
        }
    }
}
