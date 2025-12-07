package com.example.myapplication.UI.Home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast; // Thêm Toast để hiển thị thông báo khi nhấn banner

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.example.myapplication.UI.Cart.CartActivity;
import com.example.myapplication.UI.product.ProductDetailActivity;
import com.example.myapplication.UI.product.ProductListActivity;
import com.example.myapplication.UI.profile.ProfileActivity;
import com.example.myapplication.UI.support.SupportActivity;
import com.example.myapplication.adapter.BannerAdapter;
import com.example.myapplication.model.Product;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ViewPager2 bannerViewPager;
    private TabLayout bannerIndicator;
    private BannerAdapter bannerAdapter;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());


    // Runner để tự động chuyển slide
    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (bannerAdapter != null && bannerAdapter.getItemCount() > 0) {
                if (bannerViewPager.getCurrentItem() == bannerAdapter.getItemCount() - 1) {
                    bannerViewPager.setCurrentItem(0);
                } else {
                    bannerViewPager.setCurrentItem(bannerViewPager.getCurrentItem() + 1);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        // --- KHỞI TẠO & CẤU HÌNH BANNER --- //
        bannerViewPager = findViewById(R.id.bannerViewPager);
        bannerIndicator = findViewById(R.id.bannerIndicator);

        // Danh sách ảnh banner
        List<Integer> bannerImages = new ArrayList<>();
        bannerImages.add(R.drawable.banner_khuyenmai1);
        bannerImages.add(R.drawable.banner_khuyenmai2);
        bannerImages.add(R.drawable.banner_khuyenmai3);
        bannerImages.add(R.drawable.banner_khuyenmai4);
        bannerImages.add(R.drawable.banner_khuyenmai5);


        // Khởi tạo adapter và gán vào ViewPager
        bannerAdapter = new BannerAdapter(bannerImages);
        bannerViewPager.setAdapter(bannerAdapter);

        // Kết nối ViewPager2 với TabLayout (hiển thị dots)
        new TabLayoutMediator(bannerIndicator, bannerViewPager,
                (tab, position) -> {
                }).attach();


        // Bắt sự kiện click từng banner
        bannerAdapter.setOnBannerClickListener(position -> {
            switch (position) {
                case 0:
                    Toast.makeText(HomeActivity.this, "Bạn đã nhấn vào khuyến mãi 1", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(HomeActivity.this, "Bạn đã nhấn vào khuyến mãi 2", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(HomeActivity.this, "Bạn đã nhấn vào món mới", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(HomeActivity.this, "Bạn đã nhấn vào khuyến mãi 4", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(HomeActivity.this, "Bạn đã nhấn vào khuyến mãi 5", Toast.LENGTH_SHORT).show();
                    break;

            }
        });

        // Tự động trượt banner mỗi 3 giây, reset lại khi người dùng vuốt
        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });

        if (!bannerImages.isEmpty()) {
            sliderHandler.postDelayed(sliderRunnable, 3000);
        }


        // CÂU CHUYỆN THƯƠNG HIỆU
        TextView tvBrandStoryTitle = findViewById(R.id.tvBrandStoryTitle);
        TextView tvBrandStoryContent = findViewById(R.id.tvBrandStoryContent);


        if (tvBrandStoryTitle != null) {
            tvBrandStoryTitle.setText("Câu chuyện thương hiệu");
        }
        if (tvBrandStoryContent != null) {
            tvBrandStoryContent.setText("Tại 22 August Coffee, chúng tôi tin rằng mỗi tách cà phê không chỉ là một thức uống, mà còn là khởi nguồn của những câu chuyện, những khoảnh khắc bình yên và niềm đam mê bất tận. Ra đời từ tình yêu với hạt cà phê nguyên bản và khát khao mang đến những trải nghiệm đáng nhớ, Hoài Nam Coffee tự hào chắt lọc tinh hoa từ những vùng đất cà phê trứ danh, để mỗi giọt đắng - ngọt đều đậm đà và chân thật nhất.\n\nChúng tôi không chỉ pha cà phê, chúng tôi kiến tạo không gian nơi bạn có thể tìm thấy chính mình, sẻ chia những kỷ niệm và nạp lại năng lượng. Hãy để Hoài Nam trở thành một phần thân thuộc trong hành trình của bạn, nơi hương thơm dẫn lối và vị ngon lưu luyến mãi không quên.");
        }
        // KẾT THÚC PHẦN CÂU CHUYỆN THƯƠNG HIỆU


        // Tìm các view để xử lý click cho danh mục và Best Seller
        LinearLayout coffeeCategory = findViewById(R.id.coffee_category);
        LinearLayout juiceCategory = findViewById(R.id.juice_category);
        LinearLayout teaCategory = findViewById(R.id.tea_category);
        LinearLayout cakeCategory = findViewById(R.id.cake_category);
        LinearLayout orangeJuiceItem = findViewById(R.id.orange_juice_item);
        LinearLayout chocoCakeItem = findViewById(R.id.choco_cake_item);
        LinearLayout matchaIceItem = findViewById(R.id.matcha_ice_item);
        LinearLayout donutsItem = findViewById(R.id.donuts_item);

        // Xử lý sự kiện click cho các danh mục
        coffeeCategory.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProductListActivity.class);
            intent.putExtra("category", "Coffee");
            startActivity(intent);
        });

        juiceCategory.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProductListActivity.class);
            intent.putExtra("category", "Juice");
            startActivity(intent);
        });

        teaCategory.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProductListActivity.class);
            intent.putExtra("category", "Tea");
            startActivity(intent);
        });

        cakeCategory.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProductListActivity.class);
            intent.putExtra("category", "Cake");
            startActivity(intent);
        });

        // BEST SELLER

        orangeJuiceItem.setOnClickListener(v -> {
            Product product = new Product(
                    "Dâu Phô Mai",
                    55000,
                    "https://i.ibb.co/FbTCdmyJ/ic-placeholder1.webp",
                    "BestSeller",
                    "Nước ép dâu tươi nguyên chất, vị chua ngọt hài hòa, thanh mát, giàu vitamin."
            );
            Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_object", product);
            startActivity(intent);
        });

        chocoCakeItem.setOnClickListener(v -> {
            Product product = new Product(
                    "Chocolate Cake",
                    50000,
                    "https://i.ibb.co/jvDn8qCn/ic-placeholder2.png",
                    "BestSeller",
                    "Bánh Choco Cake giòn rụm kết hợp lớp socola tan chảy, mang đến vị ngon khó cưỡng."
            );
            Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_object", product);
            startActivity(intent);
        });

        matchaIceItem.setOnClickListener(v -> {
            Product product = new Product(
                    "Matcha Ice",
                    59000,
                    "https://i.ibb.co/TBhcs2Jw/ic-placeholder3.png",
                    "BestSeller",
                    "Matcha Latte thơm lừng vị trà xanh tự nhiên, hòa quyện cùng sữa tươi béo ngậy."
            );
            Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_object", product);
            startActivity(intent);
        });

        donutsItem.setOnClickListener(v -> {
            Product product = new Product(
                    "Dotnut Cake",
                    40000,
                    "https://i.ibb.co/gFMNM72C/ic-placeholder4.png",
                    "BestSeller",
                    "Bánh Dotnut thơm ngon."
            );
            Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_object", product);
            startActivity(intent);
        });

    }
        @Override
    protected void onPause() {
        super.onPause();
        // Dừng tự động trượt khi Activity không còn hiển thị
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Bắt đầu lại tự động trượt khi Activity hiển thị trở lại
        if (bannerAdapter != null && bannerAdapter.getItemCount() > 0) {
            sliderHandler.postDelayed(sliderRunnable, 3000);
        }
    }

    // Mở danh sách sản phẩm Coffee
    public void goToHome(View view) {
        // Trang chủ, bạn có thể reload lại hoặc để trống
        Toast.makeText(this, "Bạn đang ở trang chủ", Toast.LENGTH_SHORT).show();
    }

    public void goToCart(View view) {
        Intent intent = new Intent(HomeActivity.this, CartActivity.class);
        startActivity(intent);
    }

    // Chuyển sang màn hình hỗ trợ
    public void goToSupport(View view) {
        try {
            Intent intent = new Intent(this, SupportActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Có lỗi xảy ra khi mở màn hình hỗ trợ.", Toast.LENGTH_SHORT).show();
        }
    }
    // Chuyển sang màn hình hồ sơ
    public void goToProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}