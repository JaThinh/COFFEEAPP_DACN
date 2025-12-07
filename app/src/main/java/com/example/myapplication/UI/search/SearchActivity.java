package com.example.myapplication.UI.search;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.UI.Detail.ProductDetailActivity;
import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private List<Product> fullProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.rv_search_results);

        productList = new ArrayList<>();
        fullProductList = new ArrayList<>();
        adapter = new ProductAdapter(productList, this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        fetchAllProducts();
        setupSearchView();
    }

    private void fetchAllProducts() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fullProductList.clear();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    if (product != null) {
                        product.setId(productSnapshot.getKey());
                        fullProductList.add(product);
                    }
                }
                filter("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchActivity.this, "Failed to load products.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String text) {
        List<Product> filteredList = new ArrayList<>();
        if (text.isEmpty()) {
            filteredList.addAll(fullProductList);
        } else {
            for (Product item : fullProductList) {
                if (item.getName() != null && item.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        adapter.updateList(filteredList);
    }

    @Override
    public void onProductClick(Product product, ImageView sharedImageView) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("PRODUCT_ID", product.getId());

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                sharedImageView,
                sharedImageView.getTransitionName());

        startActivity(intent, options.toBundle());
    }

    @Override
    public void onAddToCartClick(Product product) {
        // Handle add to cart click if needed, or leave empty
    }

    @Override
    public void onFavoriteClick(Product product, boolean isFavorite) {
        String message = isFavorite ? product.getName() + " đã được thêm vào danh sách yêu thích" : product.getName() + " đã bị xóa khỏi danh sách yêu thích";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
