package com.example.myapplication.UI.product;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Item.CartItem;
import com.example.myapplication.R;
import com.example.myapplication.model.Bill;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    private static final String TAG = "OrderDetailActivity";

    private TextView detailUserName, detailTimestamp, detailTotal, detailPaymentMethod;
    private ListView listViewItems;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        detailUserName = findViewById(R.id.detail_user_name);
        detailTimestamp = findViewById(R.id.detail_timestamp);
        detailTotal = findViewById(R.id.detail_total);
        detailPaymentMethod = findViewById(R.id.detail_payment_method);
        listViewItems = findViewById(R.id.list_view_items);

        String billId = getIntent().getStringExtra("orderId");

        // THAY ĐỔI TẠI ĐÂY: Biến userId giờ đây là effectively final
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId; // Khai báo nhưng chưa gán

        if (currentUser != null) {
            userId = currentUser.getUid(); // Gán giá trị một lần duy nhất
        } else {
            userId = null; // Gán giá trị một lần duy nhất nếu currentUser là null
        }

        if (billId == null || userId == null) {
            Toast.makeText(this, "Không tìm thấy thông tin đơn hàng hoặc người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "billId or userId is null. billId: " + billId + ", userId: " + userId);
            finish();
            return;
        }

        // Lấy dữ liệu từ nhánh "OrderHistory" nơi bạn lưu bill
        dbRef = FirebaseDatabase.getInstance().getReference("OrderHistory").child(userId).child(billId);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Fetching bill details for billId: " + billId + " under userId: " + userId);
                Log.d(TAG, "Snapshot exists: " + snapshot.exists());
                Log.d(TAG, "Snapshot has children: " + snapshot.hasChildren());
                Log.d(TAG, "Snapshot value (as String): " + snapshot.getValue());

                Bill bill = snapshot.getValue(Bill.class);

                if (bill != null) {
                    detailUserName.setText("Khách hàng: " + (bill.getUserName() != null ? bill.getUserName() : currentUser != null ? currentUser.getEmail() : "N/A"));
                    detailTimestamp.setText("Thời gian: " + bill.getTimestamp());
                    detailTotal.setText("Tổng tiền: " + String.format("%,.0f", bill.getTotal()) + " VNĐ");

                    String paymentMethod = getIntent().getStringExtra("paymentMethod");
                    detailPaymentMethod.setText("Phương thức: " + (paymentMethod != null ? paymentMethod : "Không rõ"));

                    List<String> itemStrings = new ArrayList<>();
                    if (bill.getItems() != null) {
                        for (CartItem item : bill.getItems()) {
                            if (item != null && item.getProduct() != null) {
                                itemStrings.add(item.getProduct().getName() + " x" + item.getQuantity() + " - " + String.format("%,.0f", item.getProduct().getPrice()) + " VNĐ");
                            }
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(OrderDetailActivity.this,
                            android.R.layout.simple_list_item_1, itemStrings);
                    listViewItems.setAdapter(adapter);
                } else {
                    Log.e(TAG, "Bill data is null for billId: " + billId);
                    Toast.makeText(OrderDetailActivity.this, "Không tải được chi tiết đơn hàng.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderDetailActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Firebase error: " + error.getMessage());
            }
        });
    }
}