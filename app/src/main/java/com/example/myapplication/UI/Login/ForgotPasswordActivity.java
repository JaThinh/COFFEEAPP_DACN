package com.example.myapplication.UI.Login;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityForgotPasswordBinding;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        setupListeners();
    }

    private void setupListeners() {
        binding.btnBackForgotPassword.setOnClickListener(v -> finish());
        binding.btnResetPassword.setOnClickListener(v -> handleResetPassword());
    }

    private void handleResetPassword() {
        String email = binding.etForgotEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etForgotEmail.setError("Vui lòng nhập email hợp lệ.");
            binding.etForgotEmail.requestFocus();
            return;
        }

        setLoading(true);

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Yêu cầu đã được gửi. Vui lòng kiểm tra email của bạn.", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Gửi yêu cầu thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            binding.progressBarForgotPassword.setVisibility(View.VISIBLE);
            binding.btnResetPassword.setEnabled(false);
        } else {
            binding.progressBarForgotPassword.setVisibility(View.GONE);
            binding.btnResetPassword.setEnabled(true);
        }
    }
}
