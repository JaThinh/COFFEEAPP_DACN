package com.example.myapplication.UI.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.AdminCategoryAdapter;
import com.example.myapplication.model.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminCategoryActivity extends AppCompatActivity {

    private RecyclerView rcvCategories;
    private AdminCategoryAdapter adapter;
    private List<Category> categoryList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý Danh mục");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rcvCategories = findViewById(R.id.rcvCategories);
        FloatingActionButton fabAdd = findViewById(R.id.fabAddCategory);

        rcvCategories.setLayoutManager(new LinearLayoutManager(this));
        categoryList = new ArrayList<>();
        
        adapter = new AdminCategoryAdapter(this, categoryList, new AdminCategoryAdapter.OnCategoryActionListener() {
            @Override
            public void onEdit(Category category) {
                showAddEditDialog(category);
            }

            @Override
            public void onDelete(Category category) {
                showDeleteConfirmDialog(category);
            }
        });
        
        rcvCategories.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference("categories"); // Node categories

        loadCategories();

        fabAdd.setOnClickListener(v -> showAddEditDialog(null));
    }

    private void loadCategories() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Category category = data.getValue(Category.class);
                    if (category != null) {
                        // Nếu id chưa có trong object, lấy key làm id
                        if (category.getId() == null || category.getId().isEmpty()) {
                            category.setId(data.getKey());
                        }
                        categoryList.add(category);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminCategoryActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddEditDialog(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(category == null ? "Thêm Danh mục" : "Sửa Danh mục");

        // Layout dialog đơn giản: 2 EditText
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_category, null, false); // Cần tạo layout này nếu chưa có, hoặc tạo code
        // Để nhanh, tôi tạo EditText bằng code nếu không có layout sẵn. 
        // Tuy nhiên user đã edit "dialog_add_edit_category.xml" gần đây, nên tôi sẽ dùng nó.
        
        final EditText inputName = viewInflated.findViewById(R.id.edt_category_name);
        final EditText inputImage = viewInflated.findViewById(R.id.edt_category_image);

        if (category != null) {
            inputName.setText(category.getName());
            inputImage.setText(category.getImageUrl());
        }

        builder.setView(viewInflated);

        builder.setPositiveButton(category == null ? "Thêm" : "Lưu", (dialog, which) -> {
            String name = inputName.getText().toString().trim();
            String image = inputImage.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(AdminCategoryActivity.this, "Tên không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            if (category == null) {
                // Thêm mới
                String id = mDatabase.push().getKey();
                Category newCategory = new Category(id, name, image);
                if (id != null) {
                    mDatabase.child(id).setValue(newCategory);
                }
            } else {
                // Sửa
                category.setName(name);
                category.setImageUrl(image);
                mDatabase.child(category.getId()).setValue(category);
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showDeleteConfirmDialog(Category category) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa danh mục '" + category.getName() + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    mDatabase.child(category.getId()).removeValue();
                    Toast.makeText(AdminCategoryActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
