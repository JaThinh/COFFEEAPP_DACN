package com.example.myapplication.UI.admin;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AdminHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Chuyển thẳng đến Dashboard
        startActivity(new Intent(this, AdminDashboardActivity.class));
        finish();
    }
}
