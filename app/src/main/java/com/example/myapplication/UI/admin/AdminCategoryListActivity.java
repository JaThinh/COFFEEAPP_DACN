package com.example.myapplication.UI.admin;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class AdminCategoryListActivity extends AppCompatActivity implements AdminCategoryAdapter.OnCategoryActionListener {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddCategory;
    private DatabaseReference databaseReference;
    private List<Category> categoryList;
    private AdminCategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category_list);

        recyclerView = findViewById(R.id.rv_categories);
        fabAddCategory = findViewById(R.id.fab_add_category);

        databaseReference = FirebaseDatabase.getInstance().getReference("categories");

        categoryList = new ArrayList<>();
        adapter = new AdminCategoryAdapter(this, categoryList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabAddCategory.setOnClickListener(v -> {
            startActivity(new Intent(AdminCategoryListActivity.this, AdminAddCategoryActivity.class));
        });

        fetchCategories();
    }

    private void fetchCategories() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    if(category != null){
                        category.setId(snapshot.getKey());
                        categoryList.add(category);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    @Override
    public void onEdit(Category category) {
        Intent intent = new Intent(this, AdminEditCategoryActivity.class);
        intent.putExtra("categoryId", category.getId());
        startActivity(intent);
    }

    @Override
    public void onDelete(Category category) {
        databaseReference.child(category.getId()).removeValue();
    }
}
