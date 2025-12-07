package com.example.myapplication.Admin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.model.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageProductsActivity extends AppCompatActivity implements ProductAdapter.OnAdminItemActionListener {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddProduct;
    private ProductAdapter adapter;
    private List<Product> productList;
    private DatabaseReference productRef;

    private static final int ADD_PRODUCT_REQUEST_CODE = 100;
    private static final int EDIT_PRODUCT_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        recyclerView = findViewById(R.id.recyclerViewProducts);
        fabAddProduct = findViewById(R.id.fabAddProduct);

        productRef = FirebaseDatabase.getInstance().getReference("HangHoa");
        productList = new ArrayList<>();

        // Khởi tạo adapter dành cho Admin
        adapter = new ProductAdapter(this, productList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Tải sản phẩm từ Firebase
        loadProducts();

        // Thêm sản phẩm mới
        fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(ManageProductsActivity.this, AddEditProductActivity.class);
            startActivityForResult(intent, ADD_PRODUCT_REQUEST_CODE);
        });
    }

    // Load dữ liệu sản phẩm từ Realtime Database
    private void loadProducts() {
        productRef.orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot doc : snapshot.getChildren()) {
                    Product product = doc.getValue(Product.class);
                    if (product != null) {
                        product.setId(doc.getKey()); // Gán ID để xóa/sửa
                        productList.add(product);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageProductsActivity.this, "Lỗi tải sản phẩm: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ManageProducts", "loadProducts error", error.toException());
            }
        });
    }

    // Khi trở về từ màn hình thêm/sửa sản phẩm
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ADD_PRODUCT_REQUEST_CODE) {
                Toast.makeText(this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
            } else if (requestCode == EDIT_PRODUCT_REQUEST_CODE) {
                Toast.makeText(this, "Cập nhật sản phẩm thành công!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Sửa sản phẩm
    @Override
    public void onEditProduct(Product product) {
        Intent intent = new Intent(this, AddEditProductActivity.class);
        intent.putExtra("product", product);
        startActivityForResult(intent, EDIT_PRODUCT_REQUEST_CODE);
    }

    // Xoá sản phẩm (với xác nhận)
    @Override
    public void onDeleteProduct(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Xoá sản phẩm")
                .setMessage("Bạn có chắc muốn xoá sản phẩm \"" + product.getName() + "\" không?")
                .setPositiveButton("Xoá", (dialog, which) -> deleteProduct(product))
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void deleteProduct(Product product) {
        if (product.getId() == null) {
            Toast.makeText(this, "Không thể xác định sản phẩm để xoá", Toast.LENGTH_SHORT).show();
            return;
        }

        productRef.child(product.getId()).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã xoá sản phẩm", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi xoá: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
