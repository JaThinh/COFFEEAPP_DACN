package com.example.myapplication.UI.Login;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etForgotEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ các View
        etForgotEmail = findViewById(R.id.etForgotEmail);
        Button btnResetPassword = findViewById(R.id.btnResetPassword);
        ImageButton btnBack = findViewById(R.id.btnBackForgotPassword);

        //  Gửi yêu cầu đặt lại mật khẩu
        btnResetPassword.setOnClickListener(v -> sendPasswordReset());
        btnBack.setOnClickListener(v -> finish());
    }

    private void sendPasswordReset() {
        String email = etForgotEmail.getText().toString().trim();

        if (email.isEmpty()) {
            showToast("Vui lòng nhập địa chỉ email của bạn.");
            return;
        }

        // Gửi email đặt lại mật khẩu qua Firebase Authentication
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("Yêu cầu đặt lại mật khẩu đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư!");
                        finish();
                    } else {
                        String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                        // - EMAIL_NOT_FOUND: Email chưa đăng ký.
                        // - INVALID_EMAIL: Email không hợp lệ.
                        if (errorMessage != null && errorMessage.contains("There is no user record corresponding to this identifier")) {
                            showToast("Email này chưa được đăng ký.");
                        }
                        else
                        {
                            showToast("Lỗi khi gửi yêu cầu: " + errorMessage);
                        }
                    }
                });
    }

    // Hàm tiện ích để hiển thị Toast
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show(); // Dùng LENGTH_LONG cho các thông báo quan trọng
    }
}