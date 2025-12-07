package com.example.myapplication.Admin;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.UI.admin.AdminHomeActivity;

// LƯU Ý: Đây là một activity cũ. Nó đã được thay thế bằng AdminHomeActivity.
// File này chỉ tồn tại để đảm bảo tính tương thích và sẽ chuyển hướng đến màn hình mới.
public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Chuyển hướng ngay lập tức đến màn hình Admin Home mới
        Intent intent = new Intent(AdminActivity.this, AdminHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Đóng activity cũ này lại
    }
}
