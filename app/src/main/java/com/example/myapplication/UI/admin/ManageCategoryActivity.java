package com.example.myapplication.UI.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.R;
import com.example.myapplication.adapter.CategoryAdapter;
import com.example.myapplication.databinding.ActivityManageCategoryBinding;
import com.example.myapplication.model.Category;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ManageCategoryActivity extends AppCompatActivity {

    private ActivityManageCategoryBinding binding;
    private DatabaseReference categoriesRef;
    private List<Category> categoryList = new ArrayList<>();
    private CategoryAdapter adapter;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ImageView currentDialogPreview;
    private String selectedBase64Image = null;
    private boolean isUsingCustomImage = false;

    private LinkedHashMap<String, String> iconMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupIconMap();
        categoriesRef = FirebaseDatabase.getInstance().getReference("categories");

        setupGalleryLauncher();
        setupToolbar();
        setupRecyclerView();
        setupListeners();
        loadCategories();
    }

    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            Bitmap resizedBitmap = resizeBitmap(bitmap, 300);
                            
                            if (currentDialogPreview != null) {
                                currentDialogPreview.setImageBitmap(resizedBitmap);
                            }

                            selectedBase64Image = bitmapToBase64(resizedBitmap);
                            isUsingCustomImage = true;

                        } catch (IOException e) {
                            Toast.makeText(this, "Lỗi đọc ảnh", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setupIconMap() {
        iconMap = new LinkedHashMap<>();
        iconMap.put("Cà phê", "ic_category_coffee");
        iconMap.put("Trà", "ic_category_tea");
        iconMap.put("Sinh tố", "ic_category_smoothie");
        iconMap.put("Bánh ngọt", "ic_category_pastry");
        iconMap.put("Tất cả (Mặc định)", "ic_category_all");
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new CategoryAdapter(this, categoryList, new CategoryAdapter.OnCategoryActionListener() {
            @Override
            public void onEditCategory(Category category) {
                showAddEditDialog(category);
            }

            @Override
            public void onDeleteCategory(Category category) {
                showDeleteConfirmation(category);
            }
        });
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCategories.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.fabAddCategory.setOnClickListener(v -> showAddEditDialog(null));
    }

    private void loadCategories() {
        binding.progressBar.setVisibility(View.VISIBLE);
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }
                adapter.notifyDataSetChanged();
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageCategoryActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAddEditDialog(Category categoryToEdit) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_category, null);
        
        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_title);
        TextInputEditText etName = dialogView.findViewById(R.id.et_category_name);
        Spinner spinnerIcon = dialogView.findViewById(R.id.spinner_category_icon);
        currentDialogPreview = dialogView.findViewById(R.id.iv_icon_preview);
        Button btnGallery = dialogView.findViewById(R.id.btn_select_gallery);

        selectedBase64Image = null;
        isUsingCustomImage = false;

        List<String> iconDisplayNames = new ArrayList<>(iconMap.keySet());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, iconDisplayNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIcon.setAdapter(spinnerAdapter);

        spinnerIcon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isUsingCustomImage) {
                    updatePreviewFromSpinner(iconDisplayNames.get(position));
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        spinnerIcon.setOnTouchListener((v, event) -> {
            isUsingCustomImage = false;
            selectedBase64Image = null;
            return false;
        });

        btnGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        if (categoryToEdit != null) {
            tvTitle.setText("Cập nhật Danh mục");
            etName.setText(categoryToEdit.getName());
            
            String currentIcon = categoryToEdit.getImageUrl(); // Sửa getIconName() thành getImageUrl()
            
            if (currentIcon != null && currentIcon.length() > 50) {
                isUsingCustomImage = true;
                selectedBase64Image = currentIcon;
                Bitmap bitmap = base64ToBitmap(currentIcon);
                if (bitmap != null) currentDialogPreview.setImageBitmap(bitmap);
            } else {
                isUsingCustomImage = false;
                int index = 0;
                for (Map.Entry<String, String> entry : iconMap.entrySet()) {
                    if (entry.getValue().equals(currentIcon)) {
                        spinnerIcon.setSelection(index);
                        break;
                    }
                    index++;
                }
            }
        } else {
            tvTitle.setText("Thêm Danh mục mới");
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", (d, w) -> d.dismiss())
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                etName.setError("Vui lòng nhập tên danh mục");
                return;
            }

            String finalIconValue;
            if (isUsingCustomImage && selectedBase64Image != null) {
                finalIconValue = selectedBase64Image;
            } else {
                String selectedDisplayName = (String) spinnerIcon.getSelectedItem();
                finalIconValue = iconMap.get(selectedDisplayName);
            }

            saveCategory(categoryToEdit, name, finalIconValue);
            dialog.dismiss();
        });
    }

    private void updatePreviewFromSpinner(String displayName) {
        String iconName = iconMap.get(displayName);
        int resId = getResources().getIdentifier(iconName, "drawable", getPackageName());
        if (resId != 0) {
            currentDialogPreview.setImageResource(resId);
        } else {
            currentDialogPreview.setImageResource(R.drawable.ic_category_placeholder);
        }
    }

    private void saveCategory(Category existingCategory, String name, String iconName) {
        String id = (existingCategory != null) ? existingCategory.getId() : categoriesRef.push().getKey();
        Category category = new Category(id, name, iconName);
        if (id != null) {
            categoriesRef.child(id).setValue(category)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Lưu thành công", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void showDeleteConfirmation(Category category) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa danh mục \"" + category.getName() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    categoriesRef.child(category.getId()).removeValue()
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private Bitmap resizeBitmap(Bitmap source, int maxLength) {
        int width = source.getWidth();
        int height = source.getHeight();
        float ratio = (float) width / height;
        
        if (width > height) {
            if (width > maxLength) {
                width = maxLength;
                height = (int) (width / ratio);
            }
        } else {
            if (height > maxLength) {
                height = maxLength;
                width = (int) (height * ratio);
            }
        }
        return Bitmap.createScaledBitmap(source, width, height, true);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap base64ToBitmap(String base64Str) {
        try {
            byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}