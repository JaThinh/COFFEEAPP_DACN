package com.example.myapplication.UI.product;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.UI.Cart.CartActivity;
import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView recyclerProducts;
    private ProductAdapter adapter;
    private List<Product> productList;

    private Button btnViewCart;
    private ImageButton btnBack;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    private String selectedCategory;
    private DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Ánh xạ View
        recyclerProducts = findViewById(R.id.recyclerProducts);
        btnViewCart = findViewById(R.id.btnViewCart);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        // Lấy category từ Intent
        selectedCategory = getIntent().getStringExtra("category");
        if (selectedCategory == null) {
            selectedCategory = "";
        }

        // Setup RecyclerView với GridLayout 2 cột
        recyclerProducts.setLayoutManager(new GridLayoutManager(this, 2));
        productList = new ArrayList<>();

        //  ProductAdapter (cho khách hàng)
        adapter = new ProductAdapter(this, productList);
        recyclerProducts.setAdapter(adapter);

        // Firebase reference
        productRef = FirebaseDatabase.getInstance().getReference("HangHoa");

        loadProducts();

        // Chuyển sang giỏ hàng khi nhấn nút
        btnViewCart.setOnClickListener(v -> {
            Intent intent = new Intent(ProductListActivity.this, CartActivity.class);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadProducts() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerProducts.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);

        // Nếu selectedCategory rỗng -> lấy tất cả sản phẩm
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot doc : snapshot.getChildren()) {
                    Product product = doc.getValue(Product.class);
                    if (product != null) {
                        product.setId(doc.getKey());
                        productList.add(product);
                    }
                }

                progressBar.setVisibility(View.GONE);

                if (productList.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    recyclerProducts.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProductListActivity.this, "Lỗi tải sản phẩm: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ProductListActivity", "Lỗi load sản phẩm: " + error.getMessage());
            }
        };

        // Nếu có category thì lọc theo category
        if (!selectedCategory.isEmpty()) {
            productRef.orderByChild("category").equalTo(selectedCategory)
                    .addListenerForSingleValueEvent(listener);
        } else {
            productRef.addListenerForSingleValueEvent(listener);
        }
    }
}
