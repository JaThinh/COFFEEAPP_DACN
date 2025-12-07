package com.example.myapplication.UI.checkout;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.UI.Home.MainHubActivity;
import com.example.myapplication.databinding.ActivityOrderSuccessfulBinding;

public class OrderSuccessfulActivity extends AppCompatActivity {

    private ActivityOrderSuccessfulBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderSuccessfulBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Lấy mã đơn hàng từ Intent
        String orderId = getIntent().getStringExtra("ORDER_ID");
        if (orderId != null && !orderId.isEmpty()) {
            String displayId = "#" + orderId.substring(Math.max(0, orderId.length() - 6));
            binding.tvOrderId.setText("Mã đơn hàng của bạn:\n" + displayId);
        }

        // Thiết lập sự kiện cho nút quay về trang chủ
        binding.btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessfulActivity.this, MainHubActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}