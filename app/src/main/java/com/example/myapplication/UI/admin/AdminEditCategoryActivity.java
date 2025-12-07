package com.example.myapplication.UI.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.Category;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AdminEditCategoryActivity extends AppCompatActivity {

    private TextInputEditText etCategoryName;
    private TextInputEditText etIconUrl; // Added for icon URL
    private Button btnUpdateCategory;
    private DatabaseReference databaseReference;
    private String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_category);

        etCategoryName = findViewById(R.id.et_category_name);
        etIconUrl = findViewById(R.id.et_icon_url); // Initialize
        btnUpdateCategory = findViewById(R.id.btn_update_category);

        categoryId = getIntent().getStringExtra("categoryId");
        if (categoryId == null) {
            Toast.makeText(this, "Category ID is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("categories").child(categoryId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Category category = snapshot.getValue(Category.class);
                if (category != null) {
                    etCategoryName.setText(category.getName());
                    if (category.getImageUrl() != null) {
                        etIconUrl.setText(category.getImageUrl());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminEditCategoryActivity.this, "Failed to load category", Toast.LENGTH_SHORT).show();
            }
        });

        btnUpdateCategory.setOnClickListener(v -> {
            String categoryName = etCategoryName.getText().toString().trim();
            String iconUrl = etIconUrl.getText().toString().trim();

            if (categoryName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("name", categoryName);
            updates.put("image", iconUrl); // Update 'image' field for Firebase

            databaseReference.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật danh mục thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Cập nhật danh mục thất bại", Toast.LENGTH_SHORT).show();
                });
        });
    }
}
