package com.example.myapplication.UI.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
// THE FIX: Import the correct OrderHistoryActivity class
import com.example.myapplication.UI.order.OrderHistoryActivity;
import com.example.myapplication.model.Order;

public class OrderConfirmationActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER = "EXTRA_ORDER";
    private static final long REDIRECT_DELAY = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        TextView tvOrderId = findViewById(R.id.tv_order_id);
        Order order = (Order) getIntent().getSerializableExtra(EXTRA_ORDER);

        if (order != null) {
            tvOrderId.setText("Mã đơn hàng: #" + order.getOrderId());
        }

        // Automatically redirect to Order History after a delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // THE FIX: Start the correct OrderHistoryActivity
            Intent intent = new Intent(OrderConfirmationActivity.this, OrderHistoryActivity.class);
            // Clear the task stack and start a new one for the history activity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Finish this confirmation activity
        }, REDIRECT_DELAY);
    }
}
