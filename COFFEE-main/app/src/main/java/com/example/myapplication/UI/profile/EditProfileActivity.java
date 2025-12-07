package com.example.myapplication.UI.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etNewName;
    private TextView tvEmail;
    private Button btnSaveProfile;
    private ImageView ivBack;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        etNewName = findViewById(R.id.etNewName);
        tvEmail = findViewById(R.id.tvEmail);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        ivBack = findViewById(R.id.ivBack);

        if (currentUser == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để chỉnh sửa thông tin.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị email từ FirebaseAuth
        String email = currentUser.getEmail();
        if (email != null) {
            tvEmail.setText(email);
        }

        loadCurrentUserName();

        btnSaveProfile.setOnClickListener(v -> saveNewName());
        ivBack.setOnClickListener(v -> finish());
    }

    private void loadCurrentUserName() {
        String uid = currentUser.getUid();
        DocumentReference userDocRef = db.collection("users").document(uid);

        userDocRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String currentName = documentSnapshot.getString("name");
                        if (currentName != null && !currentName.isEmpty()) {
                            etNewName.setText(currentName);
                        }
                    } else {
                        Toast.makeText(this, "Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải thông tin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveNewName() {
        String newName = etNewName.getText().toString().trim();

        if (newName.isEmpty()) {
            Toast.makeText(this, "Tên không được để trống.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();
        DocumentReference userDocRef = db.collection("users").document(uid);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", newName);

        userDocRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật tên thành công!", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedName", newName);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi cập nhật tên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
