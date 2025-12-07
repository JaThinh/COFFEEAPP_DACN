package com.example.myapplication.UI.order;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.UI.Home.HomeActivity;
import com.example.myapplication.databinding.ActivityOrderSuccessBinding;

public class OrderSuccessActivity extends AppCompatActivity {

    private ActivityOrderSuccessBinding binding;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderId = getIntent().getStringExtra("ORDER_ID");
        if (orderId != null && !orderId.isEmpty()) {
            String displayId = "#" + orderId.substring(Math.max(0, orderId.length() - 6));
            binding.tvOrderId.setText("Mã đơn hàng của bạn:\n" + displayId);
        }

        binding.btnContinueShopping.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        binding.btnViewOrderHistory.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, OrderHistoryActivity.class);
            startActivity(intent);
        });
    }
}