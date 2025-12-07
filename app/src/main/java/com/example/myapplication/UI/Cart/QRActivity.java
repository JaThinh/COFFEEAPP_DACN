package com.example.myapplication.UI.Cart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.UI.Home.HomeActivity;
import java.text.NumberFormat;
import java.util.Locale;

public class QRActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        // ===== CÁC DÒNG NÀY ĐÃ ĐƯỢC SỬA LẠI CHO ĐÚNG ID TỪ FILE XML =====
        ImageView qrImage = findViewById(R.id.ivQrCode);
        TextView tvAmount = findViewById(R.id.tvAmount);
        TextView tvInstruction = findViewById(R.id.tvInstruction);
        Button btnDone = findViewById(R.id.btnConfirmPayment); // Dùng ID đã thống nhất
        // ===============================================================

        double totalPrice = getIntent().getDoubleExtra("TOTAL_PRICE", 0.0);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(totalPrice);

        // Giả sử bạn có một ảnh tên là 'qr_code' trong thư mục drawable
        // qrImage.setImageResource(R.drawable.qr_code);
        tvInstruction.setText("Quét mã để thanh toán với số tiền");
        tvAmount.setText(formattedPrice);

        btnDone.setOnClickListener(v -> {
            Intent intent = new Intent(QRActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}