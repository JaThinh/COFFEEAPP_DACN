package com.example.myapplication.UI.checkout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.UI.order.OrderHistoryActivity;
import com.example.myapplication.UI.profile.EditProfileActivity;
import com.example.myapplication.adapter.CheckoutSummaryAdapter;
import com.example.myapplication.manager.CartManager;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.Order;
import com.example.myapplication.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CheckoutActivity extends AppCompatActivity {

    public static final String EXTRA_CART_ITEMS = "EXTRA_CART_ITEMS";
    public static final String EXTRA_TOTAL_AMOUNT = "EXTRA_TOTAL_AMOUNT";

    private RecyclerView orderSummaryRecyclerView;
    private CheckoutSummaryAdapter adapter;
    private TextView totalAmountTextView, userAddressTextView, subtotalTextView, btnChangeAddress, tvUserName, tvUserPhone;
    private RadioGroup paymentMethodRadioGroup;
    private MaterialButton confirmOrderButton;
    private List<CartItem> cartItems;
    private double totalAmount;
    private double subtotal;
    private ProgressBar progressBar;

    private final ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String newName = data.getStringExtra(EditProfileActivity.EXTRA_NAME);
                    String newPhone = data.getStringExtra(EditProfileActivity.EXTRA_PHONE);
                    String newAddress = data.getStringExtra(EditProfileActivity.EXTRA_ADDRESS);

                    tvUserName.setText(newName);
                    tvUserPhone.setText(newPhone);
                    userAddressTextView.setText(newAddress);

                    updateUserProfileInFirebase(newName, newPhone, newAddress);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getExtras() != null) {
            cartItems = getIntent().getParcelableArrayListExtra(EXTRA_CART_ITEMS);
        } else {
            cartItems = new ArrayList<>();
        }

        initViews();
        loadUserInfo();
        calculatePrices();
        setupRecyclerView();
        displayOrderSummary();
        setupClickListeners();
    }

    private void initViews() {
        orderSummaryRecyclerView = findViewById(R.id.rv_order_summary);
        totalAmountTextView = findViewById(R.id.tv_total_amount);
        subtotalTextView = findViewById(R.id.tv_subtotal);
        
        userAddressTextView = findViewById(R.id.tv_user_address);
        btnChangeAddress = findViewById(R.id.btn_change_address);
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserPhone = findViewById(R.id.tv_user_phone);
        paymentMethodRadioGroup = findViewById(R.id.rg_payment_method);
        confirmOrderButton = findViewById(R.id.btn_confirm_order);
        progressBar = findViewById(R.id.progress_bar_checkout);
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            confirmOrderButton.setEnabled(false);
            confirmOrderButton.setText("Đang xử lý...");
        } else {
            progressBar.setVisibility(View.GONE);
            confirmOrderButton.setEnabled(true);
            confirmOrderButton.setText("Xác nhận đơn hàng");
        }
    }

    private void setupClickListeners() {
        btnChangeAddress.setOnClickListener(v -> openEditProfile());
        confirmOrderButton.setOnClickListener(v -> placeOrder());
    }

    private void calculatePrices() {
        subtotal = 0;
        if (cartItems != null) {
            for (CartItem item : cartItems) {
                if (item != null) {
                    subtotal += item.getProductPrice() * item.getQuantity();
                }
            }
        }
        // Removed shippingFee and discountAmount
        totalAmount = subtotal; 
        if (totalAmount < 0) totalAmount = 0;
    }

    private void setupRecyclerView() {
        adapter = new CheckoutSummaryAdapter(cartItems);
        orderSummaryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderSummaryRecyclerView.setAdapter(adapter);
    }

    private void displayOrderSummary() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        subtotalTextView.setText(currencyFormat.format(subtotal));
        totalAmountTextView.setText(currencyFormat.format(totalAmount));
    }
    
    private void openEditProfile() {
        Intent intent = new Intent(CheckoutActivity.this, EditProfileActivity.class);
        intent.putExtra(EditProfileActivity.EXTRA_NAME, tvUserName.getText().toString());
        intent.putExtra(EditProfileActivity.EXTRA_PHONE, tvUserPhone.getText().toString());
        intent.putExtra(EditProfileActivity.EXTRA_ADDRESS, userAddressTextView.getText().toString());
        editProfileLauncher.launch(intent);
    }

    private boolean isDeliveryInfoIncomplete() {
        String name = tvUserName.getText().toString();
        String phone = tvUserPhone.getText().toString();
        String address = userAddressTextView.getText().toString();
        if (name == null || phone == null || address == null) return true;
        return name.contains("Chưa có") || phone.contains("Chưa có") || address.contains("Chưa có");
    }

    private void placeOrder() {
        if (paymentMethodRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isDeliveryInfoIncomplete()) {
            Toast.makeText(this, "Vui lòng cập nhật đầy đủ thông tin giao hàng.", Toast.LENGTH_LONG).show();
            openEditProfile();
            return;
        }

        int selectedPaymentId = paymentMethodRadioGroup.getCheckedRadioButtonId();
        if (selectedPaymentId == R.id.rb_momo || selectedPaymentId == R.id.rb_vcb) {
            int qrDrawableId = selectedPaymentId == R.id.rb_momo ? R.drawable.qr_momo : R.drawable.qr_payment;
            showQrDialog(qrDrawableId);
        } else {
            runOrderCreationTask();
        }
    }

    private void runOrderCreationTask() {
        showLoading(true);

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để đặt hàng", Toast.LENGTH_SHORT).show();
            showLoading(false);
            return;
        }
        final String userId = currentUser.getUid();
        final String customerName = tvUserName.getText().toString();
        final String phoneNumber = tvUserPhone.getText().toString();
        final String shippingAddress = userAddressTextView.getText().toString();
        final int selectedPaymentId = paymentMethodRadioGroup.getCheckedRadioButtonId();
        final List<CartItem> itemsForOrder = new ArrayList<>(cartItems != null ? cartItems : new ArrayList<>());

        final double finalTotalAmount = totalAmount;

        new Thread(() -> {
            String paymentMethod;
            if (selectedPaymentId == R.id.rb_cash_on_delivery) {
                paymentMethod = "Thanh toán khi nhận hàng";
            } else if (selectedPaymentId == R.id.rb_momo) {
                paymentMethod = "Ví MoMo";
            } else if (selectedPaymentId == R.id.rb_vcb) {
                paymentMethod = "Chuyển khoản ngân hàng";
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(CheckoutActivity.this, "Phương thức thanh toán không hợp lệ", Toast.LENGTH_SHORT).show();
                    showLoading(false);
                });
                return;
            }

            DatabaseReference userOrdersRef = FirebaseDatabase.getInstance().getReference("orders").child(userId);
            String orderId = userOrdersRef.push().getKey();
            long orderDate = System.currentTimeMillis();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                itemsForOrder.removeIf(Objects::isNull);
            }
            
            Order order = new Order(orderId, userId, itemsForOrder, finalTotalAmount, orderDate, customerName, phoneNumber, shippingAddress, paymentMethod, "Pending");
            
            userOrdersRef.child(orderId).setValue(order).addOnCompleteListener(task -> {
                
                if (task.isSuccessful()) {
                    // --- LƯU LỊCH SỬ ĐƠN HÀNG ĐỂ AI HỌC ---
                    saveOrderHistoryForAI(userId, itemsForOrder);
                    // ----------------------------------------

                    runOnUiThread(() -> showLoading(false));
                    CartManager.getInstance().clearCart();
                    Intent intent = new Intent(CheckoutActivity.this, OrderHistoryActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    runOnUiThread(() -> showLoading(false));
                    String errorMessage = "Không thể đặt hàng. Vui lòng thử lại.";
                    if (task.getException() != null) {
                        errorMessage = "Lỗi: " + task.getException().getMessage();
                        Log.e("CheckoutActivity", "Firebase Database Error", task.getException());
                    }
                    final String finalMsg = errorMessage;
                    runOnUiThread(() -> Toast.makeText(CheckoutActivity.this, finalMsg, Toast.LENGTH_LONG).show());
                }
            });
        }).start();
    }

    // Hàm mới: Lưu lịch sử món ăn vào nhánh riêng cho AI học
    private void saveOrderHistoryForAI(String userId, List<CartItem> items) {
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("order_history");
        
        // Tạo chuỗi danh sách các món
        StringBuilder itemsBuilder = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            itemsBuilder.append(items.get(i).getProductName());
            if (i < items.size() - 1) {
                itemsBuilder.append(", ");
            }
        }
        String orderContent = itemsBuilder.toString();
        
        // Lưu vào Firebase theo timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());
        historyRef.child(timestamp).setValue(orderContent);
    }

    private void showQrDialog(int qrDrawableId) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_qr_code);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView qrImageView = dialog.findViewById(R.id.iv_qr_code);
        qrImageView.setImageResource(qrDrawableId);

        MaterialButton btnDone = dialog.findViewById(R.id.btn_done_scanning);
        btnDone.setOnClickListener(v -> {
            dialog.dismiss();
            runOrderCreationTask();
        });

        dialog.show();
    }

    private void loadUserInfo() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            tvUserName.setText(TextUtils.isEmpty(user.getName()) ? "Chưa có tên" : user.getName());
                            tvUserPhone.setText(TextUtils.isEmpty(user.getPhone()) ? "Chưa có SĐT" : user.getPhone());
                            userAddressTextView.setText(TextUtils.isEmpty(user.getAddress()) ? "Chưa có địa chỉ" : user.getAddress());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CheckoutActivity.this, "Lỗi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateUserProfileInFirebase(String name, String phone, String address) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", name);
            updates.put("phone", phone);
            updates.put("address", address);
            userRef.updateChildren(updates)
                    .addOnSuccessListener(aVoid -> Toast.makeText(CheckoutActivity.this, "Đã cập nhật thông tin", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(CheckoutActivity.this, "Lỗi cập nhật thông tin", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
