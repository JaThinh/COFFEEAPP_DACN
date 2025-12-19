package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.ProductGrid;
import com.bumptech.glide.Glide;
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
            
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            productPrice.setText(currencyFormat.format(product.getPriceAsDouble()));
            
            favoriteButton.setSelected(product.isFavorite());

            // Try load remote URL first, then fallback to drawable resource, otherwise placeholder
            String imageUrl = product.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_coffee)
                        .error(R.drawable.ic_coffee)
                        .into(productImage);
            } else if (product.getImageResId() != 0) {
                productImage.setImageResource(product.getImageResId());
            } else {
                productImage.setImageResource(R.drawable.ic_coffee);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });

            addToCartButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToCartClick(product, v);
                }
            });

            favoriteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFavoriteClick(product, getAdapterPosition());
                }
            });
        }
    }
}
