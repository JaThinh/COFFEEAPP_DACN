package com.example.myapplication.UI.Rating;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityAddRatingBinding;
import com.example.myapplication.model.Product;
import com.example.myapplication.model.Rating;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class AddRatingActivity extends AppCompatActivity {

    private ActivityAddRatingBinding binding;
    private String productId;
    private String orderId;

    private DatabaseReference productRef;
    private DatabaseReference ratingRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRatingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productId = getIntent().getStringExtra("PRODUCT_ID");
        orderId = getIntent().getStringExtra("ORDER_ID");

        if (productId == null || orderId == null) {
            Toast.makeText(this, "Thiếu thông tin sản phẩm hoặc đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        productRef = FirebaseDatabase.getInstance().getReference("products").child(productId);
        ratingRef = productRef.child("ratings");

        setupToolbar();
        loadProductInfo();
        setupListeners();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadProductInfo() {
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    binding.tvProductName.setText(product.getName());
                    Glide.with(AddRatingActivity.this)
                            .load(product.getImageUrl())
                            .placeholder(R.drawable.coffee_image)
                            .into(binding.ivProductImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void setupListeners() {
        binding.btnSubmitRating.setOnClickListener(v -> submitRating());

        // Add listeners for suggestion chips
        for (int i = 0; i < binding.chipGroupSuggestions.getChildCount(); i++) {
            Chip chip = (Chip) binding.chipGroupSuggestions.getChildAt(i);
            chip.setOnClickListener(v -> {
                String currentText = binding.etComment.getText().toString();
                String chipText = chip.getText().toString();
                if (currentText.isEmpty()) {
                    binding.etComment.setText(chipText);
                } else {
                    binding.etComment.setText(currentText + ". " + chipText);
                }
            });
        }
    }

    private void submitRating() {
        float ratingValue = binding.ratingBar.getRating();
        String comment = binding.etComment.getText().toString().trim();

        if (ratingValue == 0) {
            Toast.makeText(this, "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        String ratingId = ratingRef.push().getKey();
        String userId = currentUser.getUid();
        String userName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Người dùng ẩn danh";
        long timestamp = System.currentTimeMillis();

        Rating rating = new Rating(ratingId, userId, userName, ratingValue, comment, timestamp);

        // Save the new rating
        ratingRef.child(ratingId).setValue(rating).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // After saving, update the product's average rating using a transaction
                updateProductAverageRating(ratingValue);
            } else {
                setLoading(false);
                Toast.makeText(AddRatingActivity.this, "Gửi đánh giá thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProductAverageRating(float newRating) {
        productRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Product product = mutableData.getValue(Product.class);
                if (product == null) {
                    return Transaction.success(mutableData);
                }

                long ratingCount = product.getRatingCount();
                double averageRating = product.getRating();

                // Calculate new average
                double newAverage = ((averageRating * ratingCount) + newRating) / (ratingCount + 1);

                product.setRatingCount(ratingCount + 1);
                product.setRating(newAverage);

                mutableData.setValue(product);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                setLoading(false);
                if (committed) {
                    Toast.makeText(AddRatingActivity.this, "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddRatingActivity.this, "Không thể cập nhật đánh giá trung bình", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnSubmitRating.setEnabled(!isLoading);
    }
}
