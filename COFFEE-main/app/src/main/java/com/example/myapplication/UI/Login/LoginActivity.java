package com.example.myapplication.UI.Login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.Admin.AdminActivity;
import com.example.myapplication.UI.Home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    TextView tvForgotPassword, tvRegister;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo Firebase Auth và Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Ánh xạ các View
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);

        //  đăng nhập
        btnLogin.setOnClickListener(v -> handleLogin());

        // Quên mật khẩu
        tvForgotPassword.setOnClickListener(v ->startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class))
        );

        // "Đăng ký" --> Chuyển sang màn hình đăng ký
        tvRegister.setOnClickListener(v -> {startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    // Xử lý logic đăng nhập
    private void handleLogin() {
        String email = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Kiểm tra đầu vào
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email và mật khẩu!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Đăng nhập với Firebase Auth
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null)
                        {
                            // Lấy thông tin vai trò từ Firestore
                            checkUserRoleAndNavigate(user.getUid());
                        }
                    }
                    else
                    {
                        Toast.makeText(this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Kiểm tra người dùng và điều hướng
    private void checkUserRoleAndNavigate(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists())
                    {
                        String role = documentSnapshot.getString("role");
                        if ("admin".equals(role))
                        {
                            Toast.makeText(this, "Đăng nhập thành công với vai trò Admin!", Toast.LENGTH_SHORT).show();
                            // Chuyển đến màn hình Admin
                            startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                        } else
                        {
                            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, HomeActivity.class));
                        }
                        finish();
                    } else
                    {

                        saveUserToFirestore(uid, etUsername.getText().toString().trim(), "customer");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi kiểm tra vai trò: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Lưu thông tin người dùng lên Firestore
    private void saveUserToFirestore(String uid, String email, String role) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("role", role);
        user.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đăng nhập thành công và đã lưu thông tin người dùng!", Toast.LENGTH_SHORT).show();
                    if ("admin".equals(role))
                    {
                        // Chuyển đến màn hình Admin
                        startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                    }
                    else
                    {
                        startActivity(new Intent(this, HomeActivity.class));
                    }
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi lưu dữ liệu người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}