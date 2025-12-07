package com.example.myapplication.UI.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityProfileEditBinding;
import com.example.myapplication.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    // Constants for Intent extras
    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_PHONE = "extra_phone";
    public static final String EXTRA_ADDRESS = "extra_address";

    private ActivityProfileEditBinding binding;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để thực hiện việc này.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        // Check if we received data to pre-fill (optional, from CheckoutActivity)
        if (getIntent() != null) {
            if (getIntent().hasExtra(EXTRA_NAME)) binding.etFullName.setText(getIntent().getStringExtra(EXTRA_NAME));
            if (getIntent().hasExtra(EXTRA_PHONE)) binding.etPhone.setText(getIntent().getStringExtra(EXTRA_PHONE));
            if (getIntent().hasExtra(EXTRA_ADDRESS)) binding.etAddress.setText(getIntent().getStringExtra(EXTRA_ADDRESS));
        }

        setupToolbar();
        setupListeners();
        loadCurrentUserInfo();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        binding.btnSaveProfile.setOnClickListener(v -> saveUserProfile());
    }

    private void loadCurrentUserInfo() {
        binding.etEmail.setText(currentUser.getEmail());

        setLoading(true);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setLoading(false);
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        // Only set if empty (to avoid overwriting pre-filled data from intent if any)
                        if (binding.etFullName.getText().toString().isEmpty()) binding.etFullName.setText(user.getName());
                        binding.etRole.setText(user.getRole());
                        if (binding.etPhone.getText().toString().isEmpty()) binding.etPhone.setText(user.getPhone());
                        if (binding.etAddress.getText().toString().isEmpty()) binding.etAddress.setText(user.getAddress());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setLoading(false);
                Toast.makeText(EditProfileActivity.this, "Không thể tải thông tin hiện tại.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserProfile() {
        String newName = binding.etFullName.getText().toString().trim();
        String newPhone = binding.etPhone.getText().toString().trim();
        String newAddress = binding.etAddress.getText().toString().trim();

        if (newName.isEmpty()) {
            binding.etFullName.setError("Tên không được để trống.");
            binding.etFullName.requestFocus();
            return;
        }

        setLoading(true);

        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", newName);
        updates.put("name", newName); // Update both for compatibility
        updates.put("phone", newPhone);
        updates.put("address", newAddress);

        userRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    setLoading(false);
                    Toast.makeText(EditProfileActivity.this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                    
                    // Return result to calling activity (e.g., CheckoutActivity)
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(EXTRA_NAME, newName);
                    resultIntent.putExtra(EXTRA_PHONE, newPhone);
                    resultIntent.putExtra(EXTRA_ADDRESS, newAddress);
                    setResult(RESULT_OK, resultIntent);
                    
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(EditProfileActivity.this, "Lỗi khi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnSaveProfile.setEnabled(!isLoading);
    }
}
