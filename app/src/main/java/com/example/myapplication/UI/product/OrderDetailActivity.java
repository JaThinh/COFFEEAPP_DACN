package com.example.myapplication.UI.product;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.FirebaseOrder;
import com.example.myapplication.model.CartItem;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView detailUserName, detailTimestamp, detailTotal, detailPaymentMethod;
    private ListView listViewItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // detailUserName = findViewById(R.id.detail_user_name);
        // detailTimestamp = findViewById(R.id.detail_timestamp);
        // detailTotal = findViewById(R.id.detail_total);
        // detailPaymentMethod = findViewById(R.id.detail_payment_method);
        // listViewItems = findViewById(R.id.list_view_items);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("order")) {
            FirebaseOrder order = (FirebaseOrder) intent.getSerializableExtra("order");
            if (order != null) {
                // displayOrderDetails(order);
            }
        }
    }

    // private void displayOrderDetails(FirebaseOrder order) {
    //     detailUserName.setText("User: " + order.getCustomerName());
    //     SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
    //     detailTimestamp.setText("Time: " + sdf.format(new Date(order.getTimestamp())));
    //     detailTotal.setText("Total: " + order.getTotalPrice() + " VND");
    //     detailPaymentMethod.setText("Payment: " + order.getPaymentMethod());

    //     List<String> itemDetails = new ArrayList<>();
    //     if (order.getItems() != null) {
    //         for (CartItem item : order.getItems()) {
    //             itemDetails.add(item.getProductName() + " - " + item.getQuantity() + " x " + item.getProductPrice() + " VND");
    //         }
    //     }
    //     ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemDetails);
    //     listViewItems.setAdapter(adapter);
    // }
}
