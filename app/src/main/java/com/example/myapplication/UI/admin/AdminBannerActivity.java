package com.example.myapplication.UI.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.Banner; 
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminBannerActivity extends AppCompatActivity {

    private RecyclerView rcvBanners;
    private BannerAdapter adapter;
    private List<Banner> bannerList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_banner);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý Banner");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rcvBanners = findViewById(R.id.rcvBanners);
        Button btnAdd = findViewById(R.id.btnAddBanner);

        rcvBanners.setLayoutManager(new LinearLayoutManager(this));
        bannerList = new ArrayList<>();
        adapter = new BannerAdapter(this, bannerList);
        rcvBanners.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference("banners");

        loadBanners();

        btnAdd.setOnClickListener(v -> showAddBannerDialog());
    }

    private void loadBanners() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bannerList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Banner banner = data.getValue(Banner.class);
                    if (banner != null) {
                        banner.setId(data.getKey());
                        bannerList.add(banner);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void showAddBannerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm Banner mới");

        final EditText input = new EditText(this);
        input.setHint("Nhập URL ảnh banner");
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String url = input.getText().toString().trim();
            if (!url.isEmpty()) {
                String id = mDatabase.push().getKey();
                Banner banner = new Banner(id, url);
                if (id != null) mDatabase.child(id).setValue(banner);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // --- Inner Adapter Class ---
    private class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {
        private Context context;
        private List<Banner> list;

        public BannerAdapter(Context context, List<Banner> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Reuse simple item layout or create one. 
            // Using a simple layout with an ImageView and a Delete button.
            // I'll create a View programmatically or use item_admin_product.xml logic if reused.
            // Better to use a simple layout file. I will assume item_banner.xml or create a simple view here.
            
            // Let's inflate a simple view for now.
             View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false); 
             // Reusing item_category temporarily but mapping fields differently
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Banner banner = list.get(position);
            
            // Using item_category layout:
            // iv_category_icon -> banner image
            // tv_category_name -> URL text
            // btn_delete_category -> delete button
            
            Glide.with(context).load(banner.getImageUrl()).into(holder.img);
            holder.tvUrl.setText("Banner " + (position + 1));
            
            holder.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                    .setTitle("Xóa Banner")
                    .setMessage("Bạn có chắc muốn xóa banner này?")
                    .setPositiveButton("Xóa", (dialog, which) -> mDatabase.child(banner.getId()).removeValue())
                    .setNegativeButton("Hủy", null)
                    .show();
            });
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView img;
            android.widget.TextView tvUrl;
            View btnDelete;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.iv_category_icon);
                tvUrl = itemView.findViewById(R.id.tv_category_name);
                btnDelete = itemView.findViewById(R.id.btn_delete_category);
                // Make sure delete button is visible
                if (btnDelete != null) btnDelete.setVisibility(View.VISIBLE);
            }
        }
    }
}
