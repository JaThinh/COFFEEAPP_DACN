package com.example.myapplication.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.UI.Login.LoginActivity;
import com.example.myapplication.databinding.ActivityAddEditProductBinding;
import com.example.myapplication.model.Category;
import com.example.myapplication.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AddEditProductActivity extends AppCompatActivity {

    private ActivityAddEditProductBinding binding;
    private DatabaseReference productsRef, categoriesRef;
    private FirebaseAuth mAuth;

    private boolean isEditMode = false;
    private String productIdToEdit;
    private Product mEditingProduct = null;

    private List<Category> categoryList = new ArrayList<>();
    private ArrayAdapter<String> categoryAdapter;

    // Map để lưu tên hiển thị -> tên file drawable
    private LinkedHashMap<String, String> imageMap;
    private ArrayAdapter<String> imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(AddEditProductActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        productsRef = FirebaseDatabase.getInstance().getReference("products");
        categoriesRef = FirebaseDatabase.getInstance().getReference("categories");

        setupToolbar();
        setupImageSpinner();
        loadCategories(); // Tải danh mục từ Firebase để đồng bộ

        if (getIntent().hasExtra("product_id")) {
            isEditMode = true;
            productIdToEdit = getIntent().getStringExtra("product_id");
            binding.btnSaveProduct.setText("Cập nhật sản phẩm");
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Cập nhật sản phẩm");
            loadProductData(productIdToEdit);
        } else {
            isEditMode = false;
            binding.btnSaveProduct.setText("Thêm sản phẩm");
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Thêm sản phẩm");
        }

        binding.btnSaveProduct.setOnClickListener(v -> saveProduct());
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupImageSpinner() {
        // KHỞI TẠO DANH SÁCH ẢNH MẪU (Tên hiển thị -> Tên file drawable)
        imageMap = new LinkedHashMap<>();
        imageMap.put("Mặc định (Placeholder)", "ic_coffee_placeholder");
        
        // Cà phê
        imageMap.put("Cà phê 1", "ic_coffee");
        imageMap.put("Cà phê 2", "ic_coffee_1");
        imageMap.put("Cà phê 3", "ic_coffee_2");
        imageMap.put("Cà phê 4", "ic_coffee_3");
        
        // Trà & Trà sữa
        imageMap.put("Trà sữa", "ic_milktea");
        imageMap.put("Trà (Ly vàng)", "ic_tea_image");
        imageMap.put("Trà (Ly xanh)", "ic_tea_image_1");
        
        // Bánh ngọt
        imageMap.put("Bánh kem", "ic_cake");
        imageMap.put("Bánh ngọt 1", "ic_cake_1");
        imageMap.put("Bánh ngọt 2", "ic_cake_2");
        imageMap.put("Bánh dâu", "ic_strawberry");
        
        // Nước ép & Khác
        imageMap.put("Nước ép", "ic_juice");

        // Tạo adapter cho Spinner chọn ảnh
        List<String> imageNames = new ArrayList<>(imageMap.keySet());
        imageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, imageNames);
        imageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerImageSelect.setAdapter(imageAdapter);

        // Xử lý sự kiện chọn item để xem trước ảnh
        binding.spinnerImageSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = imageNames.get(position);
                String drawableName = imageMap.get(selectedName);
                updateImagePreview(drawableName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateImagePreview(String drawableName) {
        int resId = getResources().getIdentifier(drawableName, "drawable", getPackageName());
        if (resId != 0) {
            binding.imgPreview.setImageResource(resId);
        } else {
            binding.imgPreview.setImageResource(R.drawable.ic_coffee_placeholder);
        }
    }

    private void loadCategories() {
        List<String> categoryNames = new ArrayList<>();
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(categoryAdapter);

        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                categoryNames.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Category category = categorySnapshot.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                        categoryNames.add(category.getName());
                    }
                }
                categoryAdapter.notifyDataSetChanged();
                
                // Nếu đang edit và đã load xong danh mục -> set selection
                if (mEditingProduct != null) {
                    setCategorySpinnerSelection(mEditingProduct.getCategoryId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddEditProductActivity.this, "Lỗi tải danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductData(String productId) {
        productsRef.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    mEditingProduct = product;
                    populateFields(product);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddEditProductActivity.this, "Lỗi tải dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFields(Product product) {
        binding.etProductName.setText(product.getName());
        binding.etProductPrice.setText(String.valueOf(product.getPrice()));
        binding.etProductDescription.setText(product.getDescription());

        // Set chọn ảnh dựa trên tên drawable đã lưu
        setImageSpinnerSelection(product.getImageUrl());

        // Set chọn danh mục
        if (!categoryList.isEmpty()) {
            setCategorySpinnerSelection(product.getCategoryId());
        }
    }

    private void setImageSpinnerSelection(String drawableName) {
        if (drawableName == null) return;
        int index = 0;
        for (Map.Entry<String, String> entry : imageMap.entrySet()) {
            if (entry.getValue().equals(drawableName)) {
                binding.spinnerImageSelect.setSelection(index);
                return;
            }
            index++;
        }
    }

    private void setCategorySpinnerSelection(String categoryId) {
        if (categoryId == null) return;
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryId.equals(categoryList.get(i).getId())) {
                binding.spinnerCategory.setSelection(i);
                return;
            }
        }
    }

    private void saveProduct() {
        String name = binding.etProductName.getText().toString().trim();
        String priceStr = binding.etProductPrice.getText().toString().trim();
        String desc = binding.etProductDescription.getText().toString().trim();

        // Lấy tên file drawable từ selection
        String drawableFileName = "ic_coffee_placeholder";
        if (binding.spinnerImageSelect.getSelectedItem() != null) {
             String selectedImageName = binding.spinnerImageSelect.getSelectedItem().toString();
             drawableFileName = imageMap.get(selectedImageName);
        }

        // Lấy thông tin danh mục
        if (binding.spinnerCategory.getSelectedItem() == null) {
            Toast.makeText(this, "Vui lòng chờ tải danh mục hoặc thêm danh mục mới", Toast.LENGTH_SHORT).show();
            return;
        }
        int selectedCategoryPos = binding.spinnerCategory.getSelectedItemPosition();
        if (selectedCategoryPos < 0 || selectedCategoryPos >= categoryList.size()) {
             // Fallback nếu list trống hoặc lỗi
             Toast.makeText(this, "Lỗi danh mục", Toast.LENGTH_SHORT).show();
             return;
        }
        Category selectedCategory = categoryList.get(selectedCategoryPos);
        String categoryId = selectedCategory.getId();
        String categoryName = selectedCategory.getName();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Vui lòng điền tên và giá sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = 0;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng Product
        // Lưu ý: Dùng constructor hoặc setter phù hợp với model Product của bạn
        // Ở đây tôi dùng setter để an toàn
        String id = isEditMode ? productIdToEdit : productsRef.push().getKey();
        
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        product.setDescription(desc);
        product.setImageUrl(drawableFileName); // Lưu tên drawable
        product.setCategoryId(categoryId);
        product.setCategoryName(categoryName);
        if (isEditMode && mEditingProduct != null) {
            product.setSoldCount(mEditingProduct.getSoldCount()); // Giữ lại số lượng đã bán
            product.setRating(mEditingProduct.getRating());       // Giữ lại đánh giá
            product.setRatingCount(mEditingProduct.getRatingCount());
            product.setFavorite(mEditingProduct.isFavorite());
        }

        if (id != null) {
            binding.btnSaveProduct.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);
            
            productsRef.child(id).setValue(product)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddEditProductActivity.this, isEditMode ? "Cập nhật thành công" : "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                        binding.btnSaveProduct.setEnabled(true);
                        binding.progressBar.setVisibility(View.GONE);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddEditProductActivity.this, "Thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.btnSaveProduct.setEnabled(true);
                        binding.progressBar.setVisibility(View.GONE);
                    });
        }
    }
}