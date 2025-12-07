package com.example.myapplication.UI.qr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.UI.order.OrderSuccessActivity;
import com.example.myapplication.databinding.ActivityQrCodeBinding;

public class QRCodeActivity extends AppCompatActivity {

    private ActivityQrCodeBinding binding;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderId = getIntent().getStringExtra("ORDER_ID");
        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy mã đơn hàng.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.btnConfirmPayment.setOnClickListener(v -> {
            Intent intent = new Intent(QRCodeActivity.this, OrderSuccessActivity.class);
            intent.putExtra("ORDER_ID", orderId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}