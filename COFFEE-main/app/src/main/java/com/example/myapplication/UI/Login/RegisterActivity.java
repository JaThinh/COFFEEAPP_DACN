package com.example.myapplication.UI.Login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.UI.Home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etRegisterEmail, etRegisterPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        Button btnRegisterUser = findViewById(R.id.btnRegisterUser);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnRegisterUser.setOnClickListener(v -> registerUser());
        btnBack.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etRegisterEmail.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();

        if (!validateInputs(firstName, lastName, email, password)) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveNewUserToFirestore(user.getUid(), firstName, lastName, email);
                        }
                    } else {
                        showToast("Đăng ký thất bại: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    private boolean validateInputs(String firstName, String lastName, String email, String password) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin!");
            return false;
        }
        if (password.length() < 6) {
            showToast("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }
        return true;
    }

    private void saveNewUserToFirestore(String uid, String firstName, String lastName, String email) {
        String fullName = firstName + " " + lastName;

        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("name", fullName);
        user.put("email", email);
        user.put("role", "customer");
        user.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    showToast("Đăng ký thành công!");
                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    showToast("Lỗi khi lưu thông tin người dùng: " + e.getMessage());
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
