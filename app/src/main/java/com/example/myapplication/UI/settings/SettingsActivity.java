package com.example.myapplication.UI.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.UI.LandingActivity;
import com.example.myapplication.UI.profile.EditProfileActivity;
import com.example.myapplication.databinding.ActivitySettingsBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupActions();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupActions() {
        // Account
        binding.tvEditProfile.setOnClickListener(v -> {
             startActivity(new Intent(this, EditProfileActivity.class));
        });

        binding.tvChangePassword.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Đổi mật khẩu đang phát triển", Toast.LENGTH_SHORT).show();
        });

        // General
        binding.switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String status = isChecked ? "Bật" : "Tắt";
            Toast.makeText(this, "Thông báo: " + status, Toast.LENGTH_SHORT).show();
        });

        binding.tvLanguage.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Đổi ngôn ngữ đang phát triển", Toast.LENGTH_SHORT).show();
        });

        // Support
        binding.tvHelpCenter.setOnClickListener(v -> {
             Toast.makeText(this, "Trung tâm trợ giúp đang phát triển", Toast.LENGTH_SHORT).show();
        });

        binding.tvPrivacyPolicy.setOnClickListener(v -> {
             Toast.makeText(this, "Chính sách bảo mật đang phát triển", Toast.LENGTH_SHORT).show();
        });

        binding.tvAboutUs.setOnClickListener(v -> {
             Toast.makeText(this, "Aura Cafe v1.0", Toast.LENGTH_SHORT).show();
        });

        // Logout
        binding.btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LandingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
