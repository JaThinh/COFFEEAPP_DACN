package com.example.myapplication.UI.order;

import android.os.Bundle;
// ... (các import khác)
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.Order;
import com.example.myapplication.model.CartItem; // Import CartItem

public class OrderDetailActivity extends AppCompatActivity {
    // ... (các biến khác)

    private Order order; // Giả sử bạn có biến order
    // private OrderDetailAdapter adapter; // Giả sử bạn có adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_order_detail); // Đảm bảo có layout này
        // ... code khởi tạo khác
        displayOrderDetails(); // Gọi phương thức này nếu nó là nơi gây lỗi
    }

    private void displayOrderDetails() {
        // Sửa lỗi: cannot find symbol getItems() - Đã có trong Order.java
        if (order != null && order.getCartItems() != null) {
            // Cập nhật adapter
            // Giả định bạn có một OrderDetailAdapter và phương thức updateItems
            // if (adapter != null) {
            //     adapter.updateItems(order.getCartItems());
            // }
        }
        // ...
    }
    // ...
}