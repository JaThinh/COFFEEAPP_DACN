package com.example.myapplication.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.UI.Login.LoginActivity;
import com.example.myapplication.Admin.ChatWithCustomerActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {
    // Khai báo  Button
    private FirebaseAuth mAuth;
    Button btnManageProducts, btnManageUsers, btnCustomerSupport, btnLogoutAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ các View từ layout
        btnManageProducts = findViewById(R.id.btnManageProducts);

        btnCustomerSupport = findViewById(R.id.btnCustomerSupport);
        btnLogoutAdmin = findViewById(R.id.btnLogoutAdmin);

        // Xử lý sự kiện cho nút Quản lý Sản phẩm
        btnManageProducts.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, ManageProductsActivity.class);
            startActivity(intent);
        });



        // Xử lý sự kiện cho nút Hỗ trợ Khách hàng (btnCustomerSupport)
        btnCustomerSupport.setOnClickListener(v -> {
            Toast.makeText(AdminActivity.this, "Chuyển đến màn hình Hỗ trợ Khách hàng", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AdminActivity.this, ChatWithCustomerActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện cho nút Đăng xuất Admin
        btnLogoutAdmin.setOnClickListener(v -> {
            mAuth.signOut(); // Đăng xuất khỏi Firebase
            Toast.makeText(AdminActivity.this, "Đã đăng xuất tài khoản Admin.", Toast.LENGTH_SHORT).show();

            // Chuyển về màn hình đăng nhập sau khi đăng xuất
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}