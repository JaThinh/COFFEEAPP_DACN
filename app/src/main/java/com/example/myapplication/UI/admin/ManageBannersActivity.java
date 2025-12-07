package com.example.myapplication.UI.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.R;
import com.example.myapplication.adapter.BannerAdapter;
import com.example.myapplication.databinding.ActivityManageBannersBinding;
import com.example.myapplication.model.Banner;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ManageBannersActivity extends AppCompatActivity implements BannerAdapter.OnBannerListener {

    private ActivityManageBannersBinding binding;
    private BannerAdapter adapter;
    private List<Banner> bannerList;
    private DatabaseReference bannersRef;
    // private StorageReference storageRef; // Không cần dùng Storage nữa

    // Danh sách tên file ảnh trong Drawable (Cần đảm bảo tên file chính xác)
    private final String[] bannerNames = {
            "banner_khuyenmai1",
            "banner_khuyenmai2",
            "banner_khuyenmai3",
            "banner_khuyenmai5"
    };

    // Map ánh xạ từ tên hiển thị sang Resource ID để preview
    private int[] bannerResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageBannersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bannersRef = FirebaseDatabase.getInstance().getReference("banners");
        // storageRef = FirebaseStorage.getInstance().getReference("banner_images");

        // Khởi tạo danh sách Resource ID tương ứng
        initBannerResources();

        setupToolbar();
        setupRecyclerView();
        loadBanners();

        // Thay đổi sự kiện click: Mở Dialog chọn ảnh có sẵn
        binding.btnAddBanner.setOnClickListener(v -> showAddBannerDialog());
    }

    private void initBannerResources() {
        bannerResources = new int[bannerNames.length];
        for (int i = 0; i < bannerNames.length; i++) {
            int resId = getResources().getIdentifier(bannerNames[i], "drawable", getPackageName());
            if (resId == 0) {
                // Nếu không tìm thấy, dùng ảnh placeholder
                resId = R.drawable.ic_coffee_placeholder; 
            }
            bannerResources[i] = resId;
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản Lý Banner");
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        bannerList = new ArrayList<>();
        adapter = new BannerAdapter(bannerList, this);
        binding.rvBanners.setLayoutManager(new LinearLayoutManager(this));
        binding.rvBanners.setAdapter(adapter);
    }

    private void loadBanners() {
        setLoading(true);
        bannersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bannerList.clear();
                for (DataSnapshot bannerSnapshot : snapshot.getChildren()) {
                    Banner banner = bannerSnapshot.getValue(Banner.class);
                    if (banner != null) {
                        bannerList.add(banner);
                    }
                }
                adapter.notifyDataSetChanged();
                setLoading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageBannersActivity.this, "Lỗi tải banner: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                setLoading(false);
            }
        });
    }

    private void showAddBannerDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_banner);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        Spinner spnBannerSelect = dialog.findViewById(R.id.spn_banner_select);
        ImageView imgBannerPreview = dialog.findViewById(R.id.img_banner_preview);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel_dialog);
        Button btnSave = dialog.findViewById(R.id.btn_save_dialog);

        // Setup Spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bannerNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnBannerSelect.setAdapter(spinnerAdapter);

        // Handle Spinner Selection
        spnBannerSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                imgBannerPreview.setImageResource(bannerResources[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            int selectedPosition = spnBannerSelect.getSelectedItemPosition();
            if (selectedPosition >= 0 && selectedPosition < bannerNames.length) {
                String selectedBannerName = bannerNames[selectedPosition];
                saveBannerToFirebase(selectedBannerName);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void saveBannerToFirebase(String bannerName) {
        setLoading(true);
        
        String bannerId = bannersRef.push().getKey();
        if (bannerId == null) {
            Toast.makeText(this, "Lỗi tạo ID", Toast.LENGTH_SHORT).show();
            setLoading(false);
            return;
        }

        // Lưu tên file (ví dụ: "banner_khuyenmai1") vào trường imageUrl
        Banner banner = new Banner(bannerId, bannerName);
        
        bannersRef.child(bannerId).setValue(banner).addOnCompleteListener(task -> {
            setLoading(false);
            if (task.isSuccessful()) {
                Toast.makeText(this, "Thêm banner thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi lưu banner", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteClick(Banner banner) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa Banner")
                .setMessage("Bạn có chắc chắn muốn xóa banner này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteBannerFromFirebase(banner);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteBannerFromFirebase(Banner banner) {
        setLoading(true);
        // Chỉ cần xóa dữ liệu trong Database, không cần xóa Storage vì ảnh là resource có sẵn
        bannersRef.child(banner.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã xóa banner", Toast.LENGTH_SHORT).show();
                    setLoading(false);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi xóa banner: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    setLoading(false);
                });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.btnAddBanner.setEnabled(false);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnAddBanner.setEnabled(true);
        }
    }
}