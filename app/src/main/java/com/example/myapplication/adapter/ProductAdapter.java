package com.example.myapplication.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.R;
import com.example.myapplication.model.Product;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private final OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product, ImageView sharedImageView);
        void onAddToCartClick(Product product);
        default void onAddToCartClick(Product product, ImageView productImageView) {
            onAddToCartClick(product);
        }
        void onFavoriteClick(Product product, boolean isFavorite);
    }

    public ProductAdapter(List<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_grid, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_product_grid;
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public void updateList(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        ImageView favoriteIcon;
        ImageButton addToCartButton;
        TextView productName, productDescription, productPrice, productRating;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productDescription = itemView.findViewById(R.id.product_description);
            productPrice = itemView.findViewById(R.id.product_price);
            productRating = itemView.findViewById(R.id.product_rating);
            favoriteIcon = itemView.findViewById(R.id.iv_favorite);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }

        void bind(final Product product, final OnProductClickListener listener) {
            if (product == null) return;

            productName.setText(product.getName());

            if (product.getDescription() != null && !product.getDescription().isEmpty()) {
                productDescription.setText(product.getDescription());
                productDescription.setVisibility(View.VISIBLE);
            } else {
                productDescription.setVisibility(View.GONE);
            }

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            productPrice.setText(currencyFormat.format(product.getPrice()));

            if (product.getRatingCount() > 0) {
                productRating.setText(String.format(Locale.US, "%.1f", product.getRating()));
                productRating.setVisibility(View.VISIBLE);
            } else {
                productRating.setVisibility(View.GONE);
            }

            // Xử lý hiển thị ảnh: Drawable ID, URL (Glide), hoặc Drawable Name (getIdentifier)
            if (product.getImageResId() != 0) {
                // Trường hợp ảnh set cứng bằng ID (thường dùng cho data mẫu trong code)
                Glide.with(itemView.getContext())
                        .load(product.getImageResId())
                        .centerCrop()
                        .placeholder(R.drawable.product_image_placeholder)
                        .error(R.drawable.product_image_error)
                        .into(productImage);
            } else if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                String imageUrl = product.getImageUrl();
                
                if (imageUrl.startsWith("http")) {
                    // Trường hợp là URL online -> Dùng Glide
                    Glide.with(itemView.getContext())
                            .load(imageUrl)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.product_image_placeholder)
                            .error(R.drawable.product_image_error)
                            .into(productImage);
                } else {
                    // Trường hợp là tên file drawable (vd: "ic_coffee") -> Dùng getIdentifier
                    int resId = itemView.getContext().getResources().getIdentifier(imageUrl, "drawable", itemView.getContext().getPackageName());
                    if (resId != 0) {
                        productImage.setImageResource(resId);
                    } else {
                        // Nếu không tìm thấy drawable thì hiện ảnh placeholder
                        productImage.setImageResource(R.drawable.product_image_placeholder);
                    }
                }
            } else {
                productImage.setImageResource(R.drawable.product_image_placeholder);
            }

            favoriteIcon.setSelected(product.isFavorite());

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onProductClick(product, productImage);
            });

            addToCartButton.setOnClickListener(v -> {
                if (listener != null) listener.onAddToCartClick(product, productImage);
            });

            favoriteIcon.setOnClickListener(v -> {
                if (listener != null) {
                    boolean isNowFavorite = !product.isFavorite();
                    product.setFavorite(isNowFavorite);
                    favoriteIcon.setSelected(isNowFavorite);
                    listener.onFavoriteClick(product, isNowFavorite);
                }
            });
        }
    }
}