package com.example.myapplication.UI.admin;

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
import com.example.myapplication.databinding.ActivityAddEditProductBinding;
import com.example.myapplication.model.Category;
import com.example.myapplication.model.Product;
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

    private String productId = null;
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

        productsRef = FirebaseDatabase.getInstance().getReference("products");
        categoriesRef = FirebaseDatabase.getInstance().getReference("categories");

        setupToolbar();
        setupImageSpinner();
        setupListeners();
        loadCategories();

        if (getIntent().hasExtra("PRODUCT_ID")) {
            productId = getIntent().getStringExtra("PRODUCT_ID");
            loadProductDataForEdit(productId);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(productId == null ? "Thêm Sản Phẩm Mới" : "Chỉnh Sửa Sản Phẩm");
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void setupImageSpinner() {
        // KHỞI TẠO DANH SÁCH ẢNH MẪU (Dựa trên file drawable thực tế của bạn)
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

        // Tạo adapter cho Spinner
        List<String> imageNames = new ArrayList<>(imageMap.keySet());
        imageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, imageNames);
        imageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerImageSelect.setAdapter(imageAdapter);

        // Xử lý sự kiện chọn item trong Spinner
        binding.spinnerImageSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = imageNames.get(position);
                String drawableName = imageMap.get(selectedName);
                updateImagePreview(drawableName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
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

    private void setupListeners() {
        binding.btnSaveProduct.setOnClickListener(v -> saveProduct());
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
                
                // Nếu đang ở chế độ sửa và đã tải xong danh mục
                if (mEditingProduct != null) {
                   setSpinnerToValue(binding.spinnerCategory, mEditingProduct.getCategoryId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddEditProductActivity.this, "Lỗi tải danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductDataForEdit(String id) {
        productsRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    mEditingProduct = product;
                    binding.etProductName.setText(product.getName());
                    binding.etProductPrice.setText(String.valueOf(product.getPrice()));
                    binding.etProductDescription.setText(product.getDescription());
                    
                    // Set lại lựa chọn ảnh dựa trên tên file drawable đã lưu
                    String savedDrawableName = product.getImageUrl(); // Trong mô hình mới, imageUrl lưu tên drawable
                    setImageSpinnerSelection(savedDrawableName);

                    // Set spinner danh mục (nếu danh mục đã load xong)
                    if (!categoryList.isEmpty()) {
                        setSpinnerToValue(binding.spinnerCategory, product.getCategoryId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddEditProductActivity.this, "Lỗi tải dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    
    private void setImageSpinnerSelection(String drawableName) {
        if (drawableName == null) return;
        int index = 0;
        for (Map.Entry<String, String> entry : imageMap.entrySet()) {
            // Kiểm tra nếu tên lưu trong DB khớp với value trong Map
            if (entry.getValue().equals(drawableName)) {
                binding.spinnerImageSelect.setSelection(index);
                return;
            }
            index++;
        }
    }
    
    private void setSpinnerToValue(Spinner spinner, String categoryId) {
        if (categoryId == null) return;
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryId.equals(categoryList.get(i).getId())) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private void saveProduct() {
        String name = binding.etProductName.getText().toString().trim();
        String priceStr = binding.etProductPrice.getText().toString().trim();
        String description = binding.etProductDescription.getText().toString().trim();

        // Lấy tên file drawable từ selection
        String drawableFileName = "ic_coffee_placeholder"; // Mặc định
        if (binding.spinnerImageSelect.getSelectedItem() != null) {
             String selectedImageName = binding.spinnerImageSelect.getSelectedItem().toString();
             drawableFileName = imageMap.get(selectedImageName);
        }

        if (binding.spinnerCategory.getSelectedItem() == null) {
            Toast.makeText(this, "Vui lòng chờ tải danh mục", Toast.LENGTH_SHORT).show();
            return;
        }
        int selectedCategoryPos = binding.spinnerCategory.getSelectedItemPosition();
        Category selectedCategory = categoryList.get(selectedCategoryPos);
        String categoryId = selectedCategory.getId();
        String categoryName = selectedCategory.getName();

        if (TextUtils.isEmpty(name)) {
            binding.etProductName.setError("Tên không được trống");
            return;
        }
        
        if (TextUtils.isEmpty(priceStr)) {
            binding.etProductPrice.setError("Giá không được trống");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSaveProduct.setEnabled(false);

        // Lưu trực tiếp (không cần upload ảnh nữa)
        saveProductToDatabase(name, Double.parseDouble(priceStr), description, categoryId, categoryName, drawableFileName);
    }

    private void saveProductToDatabase(String name, double price, String description, String categoryId, String categoryName, String imageUrl) {
        String id = (productId == null) ? productsRef.push().getKey() : productId;

        Product product;
        if (mEditingProduct != null) {
            product = mEditingProduct;
        } else {
            product = new Product();
            product.setId(id);
            product.setSoldCount(0);
        }

        product.setName(name);
        product.setPrice(price);
        product.setDescription(description);
        product.setImageUrl(imageUrl); // Lưu tên drawable (vd: "ic_coffee")
        product.setCategoryId(categoryId);
        product.setCategoryName(categoryName);

        productsRef.child(id).setValue(product).addOnCompleteListener(task -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnSaveProduct.setEnabled(true);
            if (task.isSuccessful()) {
                Toast.makeText(AddEditProductActivity.this, "Lưu sản phẩm thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(AddEditProductActivity.this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}