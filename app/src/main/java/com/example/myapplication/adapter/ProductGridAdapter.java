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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.myapplication.R;
import com.example.myapplication.model.ProductGrid;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.ProductGridViewHolder> {

    private List<ProductGrid> productList;
    private final OnProductGridClickListener listener;

    public interface OnProductGridClickListener {
        void onProductClick(ProductGrid product);
        void onAddToCartClick(ProductGrid product, View view);
        void onFavoriteClick(ProductGrid product, int position);
    }

    public ProductGridAdapter(List<ProductGrid> productList, OnProductGridClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_grid, parent, false);
        return new ProductGridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductGridViewHolder holder, int position) {
        ProductGrid product = productList.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public void setProducts(List<ProductGrid> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    public static class ProductGridViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        ImageView favoriteButton;
        TextView productName;
        TextView productPrice;
        ImageButton addToCartButton;

        public ProductGridViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            favoriteButton = itemView.findViewById(R.id.iv_favorite);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }

        public void bind(final ProductGrid product, final OnProductGridClickListener listener) {
            productName.setText(product.getName());
            
            // Format price to currency
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            productPrice.setText(currencyFormat.format(product.getPrice()));
            
            favoriteButton.setSelected(product.isFavorite());

            // Sử dụng Glide để load ảnh mượt mà hơn và tự động resize
            Glide.with(itemView.getContext())
                    .load(product.getImageResId())
                    .transform(new CenterCrop(), new RoundedCorners(16)) // Bo góc đẹp mắt
                    .placeholder(R.drawable.ic_image_placeholder) // Ảnh chờ nếu load chậm
                    .error(R.drawable.product_image_error) // Ảnh lỗi
                    .into(productImage);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onProductClick(product);
                    }
                }
            });

            addToCartButton.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onAddToCartClick(product, v);
                    }
                }
            });

            favoriteButton.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onFavoriteClick(product, position);
                    }
                }
            });
        }
    }
}
