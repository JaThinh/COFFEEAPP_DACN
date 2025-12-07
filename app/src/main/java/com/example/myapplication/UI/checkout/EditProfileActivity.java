package com.example.myapplication.UI.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditProfileActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "EXTRA_NAME";
    public static final String EXTRA_PHONE = "EXTRA_PHONE";
    public static final String EXTRA_ADDRESS = "EXTRA_ADDRESS";

    private TextInputEditText etName, etPhone, etAddress;
    private MaterialButton btnSaveProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        btnSaveProfile = findViewById(R.id.btn_save_profile);

        // Get current info and display it
        Intent intent = getIntent();
        etName.setText(intent.getStringExtra(EXTRA_NAME));
        etPhone.setText(intent.getStringExtra(EXTRA_PHONE));
        etAddress.setText(intent.getStringExtra(EXTRA_ADDRESS));

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_NAME, name);
        resultIntent.putExtra(EXTRA_PHONE, phone);
        resultIntent.putExtra(EXTRA_ADDRESS, address);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
