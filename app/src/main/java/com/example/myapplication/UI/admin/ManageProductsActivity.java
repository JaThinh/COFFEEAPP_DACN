package com.example.myapplication.UI.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.adapter.AdminProductAdapter;
import com.example.myapplication.databinding.ActivityManageProductsBinding;
import com.example.myapplication.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageProductsActivity extends AppCompatActivity implements AdminProductAdapter.OnProductListener {

    private ActivityManageProductsBinding binding;
    private AdminProductAdapter adapter;
    private List<Product> productList;
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản Lý Sản Phẩm");
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        productsRef = FirebaseDatabase.getInstance().getReference("products");

        setupRecyclerView();
        loadProducts();

        binding.fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(ManageProductsActivity.this, AddEditProductActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        productList = new ArrayList<>();
        // Truyền 'this' làm listener
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
                        // Đảm bảo ID được set nếu Firebase không trả về trong body
                        if (product.getId() == null) {
                            product.setId(productSnapshot.getKey());
                        }
                        productList.add(product);
                    }
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageProductsActivity.this, "Lỗi tải sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

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
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Không tìm thấy ID sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }
}
