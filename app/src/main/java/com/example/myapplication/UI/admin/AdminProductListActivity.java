package com.example.myapplication.UI.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.adapter.AdminProductAdapter;
import com.example.myapplication.databinding.ActivityAdminProductListBinding;
import com.example.myapplication.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

// BƯỚC 1: Implement listener
public class AdminProductListActivity extends AppCompatActivity implements AdminProductAdapter.OnProductListener {

    private ActivityAdminProductListBinding binding;
    private AdminProductAdapter adapter;
    private List<Product> productList;
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminProductListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        productsRef = FirebaseDatabase.getInstance().getReference("products");

        setupRecyclerView();
        loadProducts();

        binding.fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(AdminProductListActivity.this, AddEditProductActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        productList = new ArrayList<>();
        // BƯỚC 2: Khởi tạo Adapter đúng, truyền "this" làm listener
        adapter = new AdminProductAdapter(productList, this);
        binding.rvProducts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvProducts.setAdapter(adapter);
    }

    private void loadProducts() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    if (product != null) {
                        if (product.getId() == null) {
                            product.setId(productSnapshot.getKey());
                        }
                        productList.add(product);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                 Toast.makeText(AdminProductListActivity.this, "Lỗi tải sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // BƯỚC 3: Thêm các hàm xử lý sự kiện Sửa/Xóa
    @Override
    public void onEditClick(Product product) {
        Intent intent = new Intent(this, AddEditProductActivity.class);
        intent.putExtra("PRODUCT_ID", product.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa '" + product.getName() + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteProductFromFirebase(product))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteProductFromFirebase(Product product) {
        if (product.getId() != null) {
            productsRef.child(product.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Không tìm thấy ID sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }
}
