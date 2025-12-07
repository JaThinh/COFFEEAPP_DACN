package com.example.myapplication.UI.Home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// PROFESSIONAL FIX: Implement ValueEventListener directly to avoid nest-host issues.
public class ProductCategoryFragment extends Fragment implements ValueEventListener {

    private static final String TAG = "ProductCategoryFragment";
    private static final String ARG_CATEGORY_ID = "category_id";

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private String categoryId;

    private Query productsQuery;

    public static ProductCategoryFragment newInstance(String categoryId) {
        ProductCategoryFragment fragment = new ProductCategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getString(ARG_CATEGORY_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_category, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_products);
        setupRecyclerView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
        if ("all".equals(categoryId)) {
            productsQuery = productsRef.orderByChild("popularity"); // Removed .limitToLast(6)
        } else {
            productsQuery = productsRef.orderByChild("categoryId").equalTo(categoryId);
        }
        productsQuery.addValueEventListener(this); // Attach the fragment itself as the listener
    }

    private void setupRecyclerView() {
        productList = new ArrayList<>();
        if (getContext() instanceof ProductAdapter.OnProductClickListener) {
            productAdapter = new ProductAdapter(productList, (ProductAdapter.OnProductClickListener) getContext());
        } else {
             // Check if context implements it, if not, we might want to handle it or avoid crashing immediately
             // For now, strictly following the pattern but safer
             if (getContext() != null) {
                 try {
                     productAdapter = new ProductAdapter(productList, (ProductAdapter.OnProductClickListener) getContext());
                 } catch (ClassCastException e) {
                     Log.e(TAG, "Context must implement OnProductClickListener");
                 }
             }
        }
        
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        if(productAdapter != null) {
            recyclerView.setAdapter(productAdapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (productsQuery != null) {
            productsQuery.removeEventListener(this); // Detach the listener
        }
    }

    // --- ValueEventListener Implementation ---

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        productList.clear();
        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
            Product product = productSnapshot.getValue(Product.class);
            if (product != null) {
                product.setId(productSnapshot.getKey());
                productList.add(product);
            }
        }
        if ("all".equals(categoryId)) {
            Collections.reverse(productList);
        }
        if (productAdapter != null) {
            productAdapter.updateList(productList);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        Log.e(TAG, "Firebase data fetch cancelled: " + error.getMessage());
    }
}
