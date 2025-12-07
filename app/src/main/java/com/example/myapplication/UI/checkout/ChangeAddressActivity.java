package com.example.myapplication.UI.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ChangeAddressActivity extends AppCompatActivity {

    public static final String EXTRA_CURRENT_ADDRESS = "EXTRA_CURRENT_ADDRESS";
    public static final String EXTRA_NEW_ADDRESS = "EXTRA_NEW_ADDRESS";

    private TextInputEditText etAddress;
    private MaterialButton btnSaveAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_address);

        etAddress = findViewById(R.id.et_address);
        btnSaveAddress = findViewById(R.id.btn_save_address);

        // Get current address and display it
        String currentAddress = getIntent().getStringExtra(EXTRA_CURRENT_ADDRESS);
        if (currentAddress != null) {
            etAddress.setText(currentAddress);
        }

        btnSaveAddress.setOnClickListener(v -> {
            String newAddress = etAddress.getText().toString().trim();
            if (TextUtils.isEmpty(newAddress)) {
                Toast.makeText(this, "Địa chỉ không được để trống", Toast.LENGTH_SHORT).show();
            } else {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_NEW_ADDRESS, newAddress);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
