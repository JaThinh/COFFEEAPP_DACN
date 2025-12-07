package com.example.myapplication.UI.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.Category;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminAddCategoryActivity extends AppCompatActivity {

    private TextInputEditText etCategoryName, etIconUrl;
    private Button btnAddCategory;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_category);

        etCategoryName = findViewById(R.id.et_category_name);
        etIconUrl = findViewById(R.id.et_icon_url); // Assuming you add this EditText to your layout
        btnAddCategory = findViewById(R.id.btn_add_category);

        databaseReference = FirebaseDatabase.getInstance().getReference("categories");

        btnAddCategory.setOnClickListener(v -> {
            String categoryName = etCategoryName.getText().toString().trim();
            String iconUrl = etIconUrl.getText().toString().trim(); // Get icon URL

            if (categoryName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
                return;
            }

            String categoryId = databaseReference.push().getKey();
            // Use the correct constructor
            Category category = new Category(categoryId, categoryName, iconUrl);

            if (categoryId != null) {
                databaseReference.child(categoryId).setValue(category)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Thêm danh mục thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Thêm danh mục thất bại", Toast.LENGTH_SHORT).show();
                    });
            }
        });
    }
}
