package com.example.myapplication.Admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.adapter.AdminRatingAdapter;
import com.example.myapplication.databinding.ActivityAdminViewRatingsBinding;
import com.example.myapplication.model.Rating;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminViewRatingsActivity extends AppCompatActivity {

    private ActivityAdminViewRatingsBinding binding;
    private AdminRatingAdapter adapter;
    private List<Rating> ratingList;
    private DatabaseReference ratingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminViewRatingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ratingsRef = FirebaseDatabase.getInstance().getReference("ratings");

        setupRecyclerView();
        setupListeners();
        loadAllRatings();
    }

    private void setupRecyclerView() {
        binding.rvRatings.setLayoutManager(new LinearLayoutManager(this));
        ratingList = new ArrayList<>();
        adapter = new AdminRatingAdapter(this, ratingList);
        binding.rvRatings.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void loadAllRatings() {
        setLoading(true);

        ratingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ratingList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot ratingSnapshot : snapshot.getChildren()) {
                        Rating rating = ratingSnapshot.getValue(Rating.class);
                        if (rating != null) {
                            ratingList.add(rating);
                        }
                    }
                    // Sort by newest first
                    Collections.sort(ratingList, (o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()));
                    binding.tvNoRatings.setVisibility(View.GONE);
                } else {
                    binding.tvNoRatings.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                setLoading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setLoading(false);
                Toast.makeText(AdminViewRatingsActivity.this, "Lỗi tải danh sách đánh giá.", Toast.LENGTH_SHORT).show();
                binding.tvNoRatings.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.rvRatings.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }
}
