package com.example.myapplication.UI.Detail;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.manager.CartManager;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.Product;
import com.example.myapplication.model.ProductGrid;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {
    
    public static final String EXTRA_PRODUCT = "product"; 

    private ImageView ivProductImage;
    private TextView tvProductName, tvProductPrice, tvProductDescription, tvQuantity;
    private ImageButton btnDecrease, btnIncrease;
    private Button btnAddToCart;
    
    private ChipGroup chipGroupSize, chipGroupSugar, chipGroupIce, chipGroupToppings;

    private Product product;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initViews();
        loadProductData();
        
        if (product != null) {
            setupUI();
            setupListeners();
        }
    }

    private void initViews() {
        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        
        btnDecrease = findViewById(R.id.btnDecrease);
        btnIncrease = findViewById(R.id.btnIncrease);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        
        chipGroupSize = findViewById(R.id.chip_group_size);
        chipGroupSugar = findViewById(R.id.chip_group_sugar);
        chipGroupIce = findViewById(R.id.chip_group_ice);
        chipGroupToppings = findViewById(R.id.chip_group_toppings);
        
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadProductData() {
        if (getIntent().hasExtra(EXTRA_PRODUCT)) {
            Object obj = getIntent().getSerializableExtra(EXTRA_PRODUCT);
            if (obj instanceof Product) {
                product = (Product) obj;
            } else if (obj instanceof ProductGrid) {
                ProductGrid pg = (ProductGrid) obj;
                product = new Product();
                product.setId("temp_" + System.currentTimeMillis());
                product.setName(pg.getName());
                product.setPrice(parsePrice(String.valueOf(pg.getPrice())));
                product.setDescription(pg.getDescription());
                product.setCategoryName(pg.getCategory());
                product.setImageResId(pg.getImageResId());
                product.setImageUrl(pg.getImageUrl());
            }
        }

        if (product == null) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupUI() {
        tvProductName.setText(product.getName());
        tvProductDescription.setText(product.getDescription());
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvProductPrice.setText(currencyFormat.format(product.getPrice()));

        String imageUrl = product.getImageUrl();
        int resId = product.getImageResId();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.length() > 100) { 
                try {
                    byte[] decodedString = Base64.decode(imageUrl, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ivProductImage.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    ivProductImage.setImageResource(R.drawable.ic_coffee_placeholder);
                }
            } else if (imageUrl.startsWith("http")) { 
                Glide.with(this).load(imageUrl).into(ivProductImage);
            } else { 
                int id = getResources().getIdentifier(imageUrl, "drawable", getPackageName());
                if (id != 0) ivProductImage.setImageResource(id);
                else ivProductImage.setImageResource(R.drawable.ic_coffee_placeholder);
            }
        } else if (resId != 0) {
            ivProductImage.setImageResource(resId);
        } else {
            ivProductImage.setImageResource(R.drawable.ic_coffee_placeholder);
        }

        addDummyChip(chipGroupSize, "M (Vừa)", true);
        addDummyChip(chipGroupSize, "L (Lớn)", false);
        addDummyChip(chipGroupSugar, "100% Đường", true);
        addDummyChip(chipGroupSugar, "70% Đường", false);
        addDummyChip(chipGroupIce, "100% Đá", true);
        addDummyChip(chipGroupIce, "50% Đá", false);
    }
    
    private void addDummyChip(ChipGroup group, String text, boolean checked) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCheckable(true);
        chip.setChecked(checked);
        group.addView(chip);
    }

    private void setupListeners() {
        btnIncrease.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
        });

        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void addToCart() {
        String selectedSize = getSelectedChipText(chipGroupSize, "M");
        String selectedSugar = getSelectedChipText(chipGroupSugar, "100%");
        String selectedIce = getSelectedChipText(chipGroupIce, "100%");
        
        CartItem item = new CartItem(
            product.getId() != null ? product.getId() : "temp_" + System.currentTimeMillis(),
            product.getName(),
            product.getPrice(),
            quantity,
            selectedSize,
            selectedSugar,
            selectedIce,
            product.getCategoryName(),
            product.getImageUrl()
        );
        item.setImageResId(product.getImageResId());

        CartManager.getInstance().addToCart(item);
        
        Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
    }
    
    private String getSelectedChipText(ChipGroup group, String defaultValue) {
        int checkedId = group.getCheckedChipId();
        if (checkedId != View.NO_ID) {
            View view = group.findViewById(checkedId);
            if (view instanceof Chip) {
                return ((Chip) view).getText().toString();
            }
        }
        return defaultValue;
    }

    private double parsePrice(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) {
            return 0.0;
        }
        try {
            // Loại bỏ tất cả các ký tự không phải là số (giữ lại dấu chấm thập phân nếu có)
            String numericString = priceStr.replaceAll("[^\\d.]", "");
            if (numericString.isEmpty()) {
                return 0.0;
            }
            return Double.parseDouble(numericString);
        } catch (NumberFormatException e) {
            return 0.0; // Trả về 0 nếu có lỗi xảy ra
        }
    }
}