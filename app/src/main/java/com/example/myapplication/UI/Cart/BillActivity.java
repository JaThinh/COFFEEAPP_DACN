package com.example.myapplication.UI.Cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.CartItem;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BillActivity extends AppCompatActivity {

    private TableLayout layoutItems;
    private TextView txtTime, txtTotal, txtBillId, txtCustomer, txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        initViews();

        // Lấy dữ liệu từ Intent (điều chỉnh cho phù hợp với dữ liệu bạn thực sự truyền)
        String orderId = getIntent().getStringExtra("orderId");
        String customerName = getIntent().getStringExtra("customerName");
        String customerEmail = getIntent().getStringExtra("customerEmail");
        double totalPrice = getIntent().getDoubleExtra("totalPrice", 0);
        List<CartItem> cartItems = (List<CartItem>) getIntent().getSerializableExtra("cartItems");

        populateUI(orderId, customerName, customerEmail, totalPrice, cartItems);
    }

    private void initViews() {
        // Ánh xạ các view từ layout activity_bill.xml
        layoutItems = findViewById(R.id.layoutItems);
        txtTime = findViewById(R.id.txtTime);
        txtTotal = findViewById(R.id.txtTotal);
        txtBillId = findViewById(R.id.txtBillId);
        txtCustomer = findViewById(R.id.txtCustomer);
        txtEmail = findViewById(R.id.txtEmail);
    }

    private void populateUI(String orderId, String customerName, String customerEmail, double totalPrice, List<CartItem> cartItems) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());

        // Hiển thị thông tin chung
        txtTime.setText("Giờ mua: " + timeFormatter.format(new Date()));
        txtBillId.setText("Mã hóa đơn: " + (orderId != null ? orderId : "N/A"));
        txtCustomer.setText("Khách hàng: " + (customerName != null ? customerName : "Khách lẻ"));
        txtEmail.setText("Email: " + (customerEmail != null ? customerEmail : "N/A"));
        txtTotal.setText("Tổng dịch vụ: " + currencyFormatter.format(totalPrice));

        // Hiển thị danh sách sản phẩm
        if (cartItems != null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            for (CartItem item : cartItems) {
                TableRow row = (TableRow) inflater.inflate(R.layout.item_bill_row, layoutItems, false);

                TextView tvName = row.findViewById(R.id.tv_item_name);
                TextView tvQty = row.findViewById(R.id.tv_item_quantity);
                TextView tvPrice = row.findViewById(R.id.tv_item_price);
                TextView tvTotal = row.findViewById(R.id.tv_item_total);

                tvName.setText(item.getProductName());
                tvQty.setText(String.valueOf(item.getQuantity()));
                tvPrice.setText(currencyFormatter.format(item.getProductPrice()));
                tvTotal.setText(currencyFormatter.format(item.getProductPrice() * item.getQuantity()));

                layoutItems.addView(row);
            }
        }
    }
}