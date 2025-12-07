package com.example.myapplication.UI.Home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityProductDetailBinding;
import com.example.myapplication.manager.CartManager;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.Product;
import com.example.myapplication.model.ProductGrid;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private Product product;
    private ProductGrid productGrid; // Hỗ trợ cả model ProductGrid nếu cần
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Nhận dữ liệu từ Intent
        if (getIntent().hasExtra("product")) {
            Object obj = getIntent().getSerializableExtra("product");
            if (obj instanceof Product) {
                product = (Product) obj;
            } else if (obj instanceof ProductGrid) {
                productGrid = (ProductGrid) obj;
                // Convert ProductGrid to Product for unified handling
                product = new Product();
                product.setId("temp_" + System.currentTimeMillis()); // Temp ID if not available
                product.setName(productGrid.getName());
                product.setPrice(productGrid.getPrice());
                product.setDescription(productGrid.getDescription());
                product.setCategoryName(productGrid.getCategory());
                // Image handling slightly different, handled below
            }
        }

        if (product == null && productGrid == null) {
            Toast.makeText(this, "Không tìm thấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
        displayProductInfo();
        setupListeners();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(""); // Để trống title để hiện ảnh đẹp hơn
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void displayProductInfo() {
        binding.tvProductName.setText(product.getName());
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        binding.tvProductPrice.setText(currencyFormat.format(product.getPrice()));
        
        binding.tvProductDescription.setText(product.getDescription());

        // Xử lý hiển thị ảnh
        String imageUrl = product.getImageUrl();
        int imageResId = 0;
        
        if (productGrid != null) {
             imageResId = productGrid.getImageResId();
        }
        
        // Ưu tiên URL / Base64
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.length() > 100) {
                // Base64
                try {
                    byte[] decodedString = Base64.decode(imageUrl, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    binding.ivProductImage.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    binding.ivProductImage.setImageResource(R.drawable.ic_coffee_placeholder);
                }
            } else if (imageUrl.startsWith("http")) {
                // URL online
                Glide.with(this).load(imageUrl).into(binding.ivProductImage);
            } else {
                // Drawable name (vd: "ic_coffee")
                int resId = getResources().getIdentifier(imageUrl, "drawable", getPackageName());
                if (resId != 0) {
                    binding.ivProductImage.setImageResource(resId);
                } else {
                    binding.ivProductImage.setImageResource(R.drawable.ic_coffee_placeholder);
                }
            }
        } else if (imageResId != 0) {
            // Resource ID từ ProductGrid
            binding.ivProductImage.setImageResource(imageResId);
        } else {
             binding.ivProductImage.setImageResource(R.drawable.ic_coffee_placeholder);
        }
    }

    private void setupListeners() {
        binding.btnIncrease.setOnClickListener(v -> {
            quantity++;
            updateQuantityUI();
        });

        binding.btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityUI();
            }
        });

        binding.btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void updateQuantityUI() {
        binding.tvQuantity.setText(String.valueOf(quantity));
    }

    private void addToCart() {
        // Tạo CartItem
        CartItem item = new CartItem();
        item.setProductId(product.getId() != null ? product.getId() : "temp_" + System.currentTimeMillis());
        item.setProductName(product.getName());
        item.setProductPrice(product.getPrice());
        item.setProductImage(product.getImageUrl()); // Lưu chuỗi ảnh (URL/Base64/DrawableName)
        item.setQuantity(quantity);
        item.setCategory(product.getCategoryName());
        
        // Các option mặc định (có thể mở rộng UI để chọn sau)
        item.setSize("M");
        item.setSugar("100%");
        item.setIce("100%");
        
        CartManager.getInstance().addToCart(item);
        
        Toast.makeText(this, "Đã thêm " + quantity + " sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        // Có thể finish() để quay lại hoặc giữ nguyên
        // finish();
    }
}