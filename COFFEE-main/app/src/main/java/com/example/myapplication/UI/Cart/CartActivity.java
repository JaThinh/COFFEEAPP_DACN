package com.example.myapplication.UI.Cart;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.CartAdapter;
import com.example.myapplication.manager.CartManager;
import com.example.myapplication.model.Bill;
import com.example.myapplication.Item.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    private TextView tvTotalPrice, tvEmptyCartMessage;
    private Button btnConfirmPurchase;
    private CartManager cartManager;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartManager = CartManager.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("bills");

        // Ánh xạ view
        ImageButton btnBack = findViewById(R.id.btnBack);
        recyclerViewCart = findViewById(R.id.recyclerCartItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvEmptyCartMessage = findViewById(R.id.tvEmptyCartMessage);
        btnConfirmPurchase = findViewById(R.id.btnConfirmPurchase);

        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartManager.getCartItems());
        recyclerViewCart.setAdapter(cartAdapter);

        // Lắng nghe thay đổi giỏ hàng
        cartAdapter.setOnCartItemChangeListener(new CartAdapter.OnCartItemChangeListener() {
            @Override
            public void onCartItemQuantityChanged() {
                updateTotalPrice();
            }

            @Override
            public void onCartItemRemoved() {
                updateTotalPrice();
                checkEmptyCartState();
            }
        });

        btnBack.setOnClickListener(v -> finish());
        updateTotalPrice();
        checkEmptyCartState();

        btnConfirmPurchase.setOnClickListener(v -> {
            if (cartManager.getCartItems().isEmpty()) {
                Toast.makeText(CartActivity.this, "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(CartActivity.this)
                    .setTitle("Chọn phương thức thanh toán")
                    .setItems(new String[]{"Tiền mặt", "Mã QR"}, (dialog, which) -> {
                        String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                        final String userName = currentUser != null ? currentUser.getEmail() : "Khách hàng ẩn danh";
                        final String userId = currentUser != null ? currentUser.getUid() : null;

                        if (userId == null) {
                            Toast.makeText(CartActivity.this, "Vui lòng đăng nhập để mua hàng.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        double total = cartManager.getTotalPrice();
                        List<CartItem> cartItems = cartManager.getCartItems();
                        String paymentMethod = (which == 0) ? "Tiền mặt" : "Mã QR";

                        String billId = databaseReference.push().getKey();

                        if (which == 1) {
                            Intent intent = new Intent(CartActivity.this, QRActivity.class);
                            intent.putExtra("userName", userName);
                            intent.putExtra("timestamp", timestamp);
                            intent.putExtra("total", total);
                            intent.putExtra("cartItems", new ArrayList<>(cartItems));
                            intent.putExtra("billId", billId);
                            intent.putExtra("paymentMethod", paymentMethod);
                            startActivity(intent);
                            return;
                        }

                        Bill bill = new Bill(userName, timestamp, total, cartItems, paymentMethod, null);
                        if (billId != null) {
                            bill.setBillId(billId);

                            // Lưu vào "bills"
                            DatabaseReference userBillsRef = FirebaseDatabase.getInstance().getReference("bills").child(userId);
                            userBillsRef.child(billId).setValue(bill);

                            // Lưu vào "OrderHistory"
                            DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("OrderHistory").child(userId);
                            historyRef.child(billId).setValue(bill);
                        }

                        Intent intent = new Intent(CartActivity.this, BillActivity.class);
                        intent.putExtra("userName", userName);
                        intent.putExtra("timestamp", timestamp);
                        intent.putExtra("total", total);
                        intent.putExtra("cartItems", new ArrayList<>(cartItems));
                        intent.putExtra("billId", billId);
                        intent.putExtra("paymentMethod", paymentMethod);
                        startActivity(intent);

                        cartManager.clearCart();
                        cartAdapter.updateCartItems(cartManager.getCartItems());
                        updateTotalPrice();
                        checkEmptyCartState();

                        Toast.makeText(CartActivity.this, "Đơn hàng đã được tạo!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private void updateTotalPrice() {
        DecimalFormat formatter = new DecimalFormat("#,###");
        double total = cartManager.getTotalPrice();
        String result = "Tổng tiền: " + formatter.format(total) + " VNĐ";
        tvTotalPrice.setText(result);
    }

    private void checkEmptyCartState() {
        boolean isEmpty = cartManager.getCartItems().isEmpty();
        tvEmptyCartMessage.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerViewCart.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        btnConfirmPurchase.setEnabled(!isEmpty);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartAdapter.updateCartItems(cartManager.getCartItems());
        updateTotalPrice();
        checkEmptyCartState();
    }
}
