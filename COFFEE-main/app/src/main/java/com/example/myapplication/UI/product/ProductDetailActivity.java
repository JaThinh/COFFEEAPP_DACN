package com.example.myapplication.UI.product;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.manager.CartManager;
import com.example.myapplication.model.Product;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        TextView productNameTextView = findViewById(R.id.detailName);
        TextView productPriceTextView = findViewById(R.id.detailPrice);
        ImageView productImageView = findViewById(R.id.detailImage);
        TextView productDescriptionTextView = findViewById(R.id.detailDescription);
        Button addToCartButton = findViewById(R.id.addToCartButton);
        ImageView backButton = findViewById(R.id.backButton);

        // NHẬN TOÀN BỘ ĐỐI TƯỢNG PRODUCT TỪ INTENT
        Product product = (Product) getIntent().getSerializableExtra("product_object");

        if (product != null) {
            // Thiết lập tên sản phẩm
            productNameTextView.setText(product.getName());

            // Định dạng và thiết lập giá sản phẩm
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
            symbols.setGroupingSeparator('.');
            DecimalFormat formatter = new DecimalFormat("#,###", symbols);
            String formattedPrice = formatter.format(product.getPrice()) + " đ";
            productPriceTextView.setText(formattedPrice);

            // Tải ảnh bằng Glide từ URL
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(productImageView);
            } else {
                productImageView.setImageResource(R.drawable.ic_launcher_background);
            }

            // Hiển thị mô tả sản phẩm (đảm bảo bạn có TextView với ID detailDescription trong layout)
            if (productDescriptionTextView != null && product.getDescription() != null && !product.getDescription().isEmpty()) {
                productDescriptionTextView.setText(product.getDescription());
            } else if (productDescriptionTextView != null) {
                // Ẩn TextView nếu không có mô tả hoặc đặt text trống
                productDescriptionTextView.setText("");
                // productDescriptionTextView.setVisibility(View.GONE); // Tùy chọn: ẩn hoàn toàn nếu không có mô tả
            }

            // Xử lý nút "Thêm vào giỏ hàng"
            addToCartButton.setOnClickListener(v -> {
                CartManager cartManager = CartManager.getInstance();
                cartManager.addProduct(product); // Sử dụng đối tượng product đã nhận
                Toast toast = Toast.makeText(ProductDetailActivity.this,
                        "Đã thêm " + product.getName() + " vào giỏ hàng!", // Sử dụng product.getName()
                        Toast.LENGTH_LONG);
                toast.show();
                v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                        .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100));
            });

        } else {
            // Trường hợp không nhận được đối tượng Product (lỗi)
            Toast.makeText(this, "Không thể tải thông tin sản phẩm. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
            finish(); // Đóng Activity nếu không có dữ liệu
        }

        // Xử lý nút "Quay lại"
        backButton.setOnClickListener(v -> {
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100)
                    .withEndAction(() -> {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100);
                        finish();
                    });
        });
    }
}