package com.example.myapplication.UI.profile;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.UI.Login.LoginActivity;
import com.example.myapplication.UI.profile.OrderHistoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvEmail, tvLogout, tvOrders, tvEditProfile;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ImageButton btnBack;

    private ActivityResultLauncher<Intent> editProfileLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        tvUserName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmail);
        tvLogout = findViewById(R.id.tvLogout);
        tvOrders = findViewById(R.id.tvOrders);
        tvEditProfile = findViewById(R.id.tvEditProfile);
        ImageButton btnBack = findViewById(R.id.btnBack);

        displayUserInfo();

        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String updatedName = result.getData().getStringExtra("updatedName");
                        if (updatedName != null && !updatedName.isEmpty()) {
                            tvUserName.setText(updatedName);
                        }
                    }
                }
        );
        // Nút back
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        } else {
            Log.e("ProfileActivity", "btnBack is null, check your layout XML");
        }

        tvEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            editProfileLauncher.launch(intent);
        });

        tvOrders.setOnClickListener(v -> {
            if (currentUser != null) {
                startActivity(new Intent(ProfileActivity.this, OrderHistoryActivity.class));
            } else {
                Toast.makeText(ProfileActivity.this, "Vui lòng đăng nhập để xem lịch sử đơn hàng", LENGTH_SHORT).show();
            }
        });

        tvLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(ProfileActivity.this, "Đăng xuất thành công", LENGTH_SHORT).show();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void displayUserInfo() {
        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Hiển thị email
            String email = currentUser.getEmail();
            if (email != null) {
                tvEmail.setText(email);
            }

            // Lấy tên từ Firestore
            DocumentReference userDocRef = db.collection("users").document(uid);
            userDocRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            if (name != null) {
                                tvUserName.setText(name);
                            }
                        } else {
                            tvUserName.setText("Không tìm thấy tên");
                        }
                    })
                    .addOnFailureListener(e -> {
                        tvUserName.setText("Lỗi tải tên");
                    });
        } else {
            tvUserName.setText("Chưa đăng nhập");
            tvEmail.setText("Không có email");
        }
    }
}
