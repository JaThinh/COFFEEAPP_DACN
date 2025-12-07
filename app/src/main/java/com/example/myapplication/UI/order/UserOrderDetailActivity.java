package com.example.myapplication.UI.order;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager; // Cần thiết nếu dùng RecyclerView
import com.example.myapplication.databinding.ActivityUserOrderDetailBinding; // Cần import binding class
import com.example.myapplication.model.Order;
import com.example.myapplication.model.CartItem; // Import CartItem
import com.example.myapplication.adapter.OrderDetailAdapter; // Import OrderDetailAdapter của bạn

import java.text.SimpleDateFormat;
import java.util.Date; // Cần thiết cho Date
import java.util.Locale;
import java.util.List; // Cần thiết nếu dùng List<CartItem>

public class UserOrderDetailActivity extends AppCompatActivity {
    private ActivityUserOrderDetailBinding binding; // Khai báo binding
    private Order order;
    private OrderDetailAdapter adapter; // Giả định OrderDetailAdapter của bạn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserOrderDetailBinding.inflate(getLayoutInflater()); // Khởi tạo binding
        setContentView(binding.getRoot());

        // Khởi tạo RecyclerView (nếu có)
        binding.rvOrderItems.setLayoutManager(new LinearLayoutManager(this));

        // Giả định order được truyền qua Intent
        if (getIntent().hasExtra("order")) {
            order = (Order) getIntent().getSerializableExtra("order"); // Hoặc dùng Parcelable
            displayOrderDetails();
        }
    }

    // Phương thức formatDate để định dạng Date
    private String formatDate(Date date) {
        if (date == null) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    private void displayOrderDetails() {
        if (order != null) {
            // SỬA: Chuyển đổi timestamp (long) thành Date trước khi định dạng
            if (order.getTimestamp() != 0) {
                binding.tvOrderDate.setText(formatDate(new Date(order.getTimestamp())));
            }

            binding.tvOrderId.setText("Mã đơn: " + order.getOrderId()); // Giả định bạn có tvOrderId
            binding.tvOrderStatus.setText("Trạng thái: " + order.getStatus()); // Giả định bạn có tvOrderStatus
            // ... Cập nhật các thông tin khác nếu có

            // Sửa lỗi: cannot find symbol getItems()
            if (order.getCartItems() != null && !order.getCartItems().isEmpty()) {
                // Giả định OrderDetailAdapter chỉ nhận Context và List<CartItem>
                adapter = new OrderDetailAdapter(this, order.getCartItems());
                // Sửa lỗi: package binding does not exist - đã được khắc phục khi import binding
                binding.rvOrderItems.setAdapter(adapter);
            } else {
                // Xử lý trường hợp không có sản phẩm trong đơn hàng
                // binding.tvNoItemsMessage.setVisibility(View.VISIBLE); // Ví dụ
            }
        }
    }
}
