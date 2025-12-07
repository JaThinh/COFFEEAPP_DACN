package com.example.myapplication.UI.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.UI.order.OrderHistoryActivity;
import com.example.myapplication.UI.Login.LoginActivity;
import com.example.myapplication.databinding.ActivityProfileBinding;
import com.example.myapplication.manager.CartManager;
import com.example.myapplication.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setupToolbar();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        // Main Profile Actions
        binding.btnLogout.setOnClickListener(v -> logoutUser());
        binding.btnEditProfile.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class)));
        
        // Menu Actions - Chỉ giữ các chức năng người dùng
        binding.btnMyOrders.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, OrderHistoryActivity.class)));
        
        // Tạm ẩn các chức năng chưa phát triển
        binding.btnAddressBook.setOnClickListener(v -> 
            Toast.makeText(ProfileActivity.this, "Sổ địa chỉ đang phát triển", Toast.LENGTH_SHORT).show());
        // Đã xóa btnSupport trong layout nếu muốn chỉ giữ các chức năng người dùng thuần túy,
        // hoặc để lại nếu muốn giữ mục Hỗ trợ trong trang cá nhân
    }

    private void loadUserProfile() {
        setLoading(true);
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        String userId = firebaseUser.getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setLoading(false);
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        populateUI(user);
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setLoading(false);
                Toast.makeText(ProfileActivity.this, "Lỗi tải dữ liệu.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUI(User user) {
        binding.tvProfileName.setText(user.getName());
        binding.tvProfileEmail.setText(user.getEmail());
        
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            binding.tvProfilePhone.setVisibility(View.VISIBLE);
            binding.tvProfilePhone.setText(user.getPhone());
        } else {
            binding.tvProfilePhone.setVisibility(View.GONE);
        }

        if (user.getAddress() != null && !user.getAddress().isEmpty()) {
            binding.tvProfileAddress.setVisibility(View.VISIBLE);
            binding.tvProfileAddress.setText(user.getAddress());
        } else {
            binding.tvProfileAddress.setVisibility(View.GONE);
        }

        String imageUrl = user.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_person) 
                    .error(R.drawable.ic_person)       
                    .circleCrop()
                    .into(binding.ivProfileAvatar);
        } else {
            binding.ivProfileAvatar.setImageResource(R.drawable.ic_person);
        }
    }

    private void showChangePasswordDialog() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Đổi mật khẩu")
                    .setMessage("Bạn có muốn nhận email để đặt lại mật khẩu không?")
                    .setPositiveButton("Gửi email", (dialog, which) -> sendPasswordResetEmail(user.getEmail()))
                    .setNegativeButton("Hủy", null)
                    .show();
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin email.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendPasswordResetEmail(String email) {
        setLoading(true);
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Email đặt lại mật khẩu đã được gửi.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Gửi email thất bại, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logoutUser() {
        mAuth.signOut();
        CartManager.getInstance().updateUserId(null);
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean isLoading) {
        binding.progressBarProfile.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}
