package com.example.myapplication.UI.Cart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.manager.CartManager;
import com.example.myapplication.model.Bill;
import com.example.myapplication.Item.CartItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class QRActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        // Khởi tạo Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("bills");

        // Ánh xạ view
        ImageView qrImage = findViewById(R.id.qrImage);
        TextView tvInstruction = findViewById(R.id.tvInstruction);
        Button btnConfirmQR = findViewById(R.id.btnConfirmQR);

        // Hiển thị ảnh QR (giả sử bạn sẽ thêm vào drawable)
        qrImage.setImageResource(R.drawable.qr_code);
        tvInstruction.setText("Quét mã QR để thanh toán qua ứng dụng ngân hàng của bạn");

        // Lấy dữ liệu từ Intent
        String userName = getIntent().getStringExtra("userName");
        String timestamp = getIntent().getStringExtra("timestamp");
        double total = getIntent().getDoubleExtra("total", 0.0);
        List<CartItem> cartItems = (List<CartItem>) getIntent().getSerializableExtra("cartItems");
        String billId = getIntent().getStringExtra("billId");
        String paymentMethod = getIntent().getStringExtra("paymentMethod");

        // Xác nhận thanh toán QR
        btnConfirmQR.setOnClickListener(v -> {
            // Lưu bill vào Firebase
            Bill bill = new Bill(userName, timestamp, total, cartItems , paymentMethod , billId);
            if (billId != null) {
                bill.setBillId(billId);
                databaseReference.child(billId).setValue(bill);
            }

            // Chuyển sang BillActivity
            Intent intent = new Intent(QRActivity.this, BillActivity.class);
            intent.putExtra("userName", userName);
            intent.putExtra("timestamp", timestamp);
            intent.putExtra("total", total);
            intent.putExtra("cartItems", new ArrayList<>(cartItems));
            intent.putExtra("billId", billId);
            intent.putExtra("paymentMethod", paymentMethod);
            startActivity(intent);

            // Xóa giỏ hàng
            CartManager.getInstance().clearCart();
        });
    }
}