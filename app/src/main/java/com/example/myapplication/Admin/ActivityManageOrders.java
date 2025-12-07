package com.example.myapplication.Admin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;

public class ActivityManageOrders extends AppCompatActivity {

    private RecyclerView rvOrders;
    // private OrderAdapter orderAdapter; // You'll need an adapter
    // private ArrayList<Order> orderList; // You'll need a model class
    private MaterialToolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_orders);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        rvOrders = findViewById(R.id.rv_orders);
        // setupRecyclerView();
        // loadOrders();
    }

    /*
    private void setupRecyclerView() {
        // orderList = new ArrayList<>();
        // orderAdapter = new OrderAdapter(this, orderList);
        // rvOrders.setLayoutManager(new LinearLayoutManager(this));
        // rvOrders.setAdapter(orderAdapter);
    }

    private void loadOrders() {
        // Logic to load orders from Firebase
    }
    */

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
