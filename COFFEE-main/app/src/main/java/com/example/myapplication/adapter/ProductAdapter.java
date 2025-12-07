package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // Thêm để test

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.manager.CartManager; // Giả định CartManager đã có
import com.example.myapplication.model.Product; // Giả định Product model đã có

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private CartManager cartManager; // Chỉ dùng cho chế độ khách hàng
    private boolean isAdminMode = false;
    private OnAdminItemActionListener adminListener; // Chỉ dùng cho chế độ Admin

    // Giao diện (interface) để gửi sự kiện từ Adapter về Activity/Fragment xử lý cho Admin
    public interface OnAdminItemActionListener {
        void onEditProduct(Product product);
        void onDeleteProduct(Product product);
    }

    // Constructor dành cho Admin Mode
    public ProductAdapter(Context context, List<Product> productList, OnAdminItemActionListener adminListener) {
        this.context = context;
        this.productList = productList;
        this.adminListener = adminListener; // Gán listener cho admin
        this.isAdminMode = true; // Bật chế độ Admin
    }

    // Constructor dành cho Customer Mode (người dùng bình thường)
    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.cartManager = CartManager.getInstance(); // Khởi tạo CartManager
        this.isAdminMode = false; // Tắt chế độ Admin
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Đặt thông tin cơ bản cho sản phẩm
        holder.textName.setText(product.getName());
        holder.textDescription.setText(product.getDescription());
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.textPrice.setText(formatter.format(product.getPrice()));

        // Tải ảnh sản phẩm bằng Glide
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background) // Ảnh tạm thời khi đang tải
                    .error(R.drawable.ic_launcher_background)     // Ảnh khi có lỗi tải
                    .into(holder.imageProduct);
        } else {
            holder.imageProduct.setImageResource(R.drawable.ic_launcher_background); // Ảnh mặc định nếu không có URL
        }

        // --- Logic hiển thị và xử lý nút bấm theo chế độ (Admin/Customer) ---

        // Ẩn tất cả các nhóm nút liên quan đến giỏ hàng và admin trước
        // Điều này đảm bảo rằng khi tái chế ViewHolder, các nút không bị hiển thị sai
        holder.quantityControlsLayout.setVisibility(View.GONE); // LinearLayout chứa btnMinus, tvQuantity, btnPlus
        holder.btnAddToCart.setVisibility(View.GONE);
        holder.adminControlsLayout.setVisibility(View.GONE); // LinearLayout chứa btnEdit, btnDelete

        if (isAdminMode) {
            // Hiển thị và xử lý các nút cho Admin
            holder.adminControlsLayout.setVisibility(View.VISIBLE); // Hiển thị nhóm nút admin

            // Xử lý sự kiện bấm nút Sửa
            holder.btnEdit.setOnClickListener(v -> {
                // Kiểm tra listener có tồn tại không trước khi gọi
                if (adminListener != null) {
                    adminListener.onEditProduct(product); // Gọi callback để Activity xử lý
                    Toast.makeText(context, "Admin: Bấm Sửa sản phẩm: " + product.getName(), Toast.LENGTH_SHORT).show(); // Test
                }
            });

            // Xử lý sự kiện bấm nút Xóa
            holder.btnDelete.setOnClickListener(v -> {
                // Kiểm tra listener có tồn tại không trước khi gọi
                if (adminListener != null) {
                    adminListener.onDeleteProduct(product); // Gọi callback để Activity xử lý
                    Toast.makeText(context, "Admin: Bấm Xóa sản phẩm: " + product.getName(), Toast.LENGTH_SHORT).show(); // Test
                }
            });

        } else {
            // Hiển thị và xử lý các nút cho Khách hàng
            int currentQuantity = cartManager.getProductQuantity(product); // Lấy số lượng sản phẩm này trong giỏ hàng

            holder.tvQuantity.setText(String.valueOf(currentQuantity)); // Cập nhật số lượng hiển thị

            if (currentQuantity > 0) {
                // Nếu đã có sản phẩm trong giỏ, hiển thị nút +/- và số lượng
                holder.quantityControlsLayout.setVisibility(View.VISIBLE);
                holder.btnAddToCart.setVisibility(View.GONE); // Ẩn nút "Thêm vào giỏ"
            } else {
                // Nếu chưa có sản phẩm trong giỏ, hiển thị nút "Thêm vào giỏ"
                holder.quantityControlsLayout.setVisibility(View.GONE);
                holder.btnAddToCart.setVisibility(View.VISIBLE);
            }

            // Xử lý sự kiện bấm nút "Thêm vào giỏ"
            holder.btnAddToCart.setOnClickListener(v -> {
                cartManager.addProduct(product); // Thêm sản phẩm vào giỏ hàng
                notifyItemChanged(position); // Thông báo cho Adapter cập nhật lại item này (để chuyển từ "Thêm vào giỏ" sang +/-)
                Toast.makeText(context, "Thêm " + product.getName() + " vào giỏ!", Toast.LENGTH_SHORT).show(); // Test
            });

            // Xử lý sự kiện bấm nút Tăng số lượng (+)
            holder.btnPlus.setOnClickListener(v -> {
                cartManager.addProduct(product); // Tăng số lượng sản phẩm trong giỏ
                notifyItemChanged(position); // Cập nhật lại item (để số lượng hiển thị thay đổi)
                Toast.makeText(context, "Tăng số lượng " + product.getName() + "!", Toast.LENGTH_SHORT).show(); // Test
            });

            // Xử lý sự kiện bấm nút Giảm số lượng (-)
            holder.btnMinus.setOnClickListener(v -> {
                cartManager.removeProduct(product); // Giảm số lượng sản phẩm trong giỏ
                notifyItemChanged(position); // Cập nhật lại item (để số lượng hiển thị thay đổi)
                Toast.makeText(context, "Giảm số lượng " + product.getName() + "!", Toast.LENGTH_SHORT).show(); // Test
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    // Phương thức để cập nhật danh sách sản phẩm và thông báo cho Adapter
    public void updateProducts(List<Product> newProducts) {
        this.productList = newProducts;
        notifyDataSetChanged(); // Thông báo toàn bộ dữ liệu đã thay đổi
    }

    // ViewHolder: Nơi ánh xạ các View từ layout item_product.xml
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textName, textPrice, textDescription, tvQuantity;
        Button btnAddToCart;
        ImageButton btnMinus, btnPlus, btnEdit, btnDelete;
        // Thêm các LinearLayout để ẩn/hiện cả nhóm dễ hơn
        View quantityControlsLayout; // Layout chứa btnMinus, tvQuantity, btnPlus
        View adminControlsLayout;    // Layout chứa btnEdit, btnDelete

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imgProduct);
            textName = itemView.findViewById(R.id.tvName);
            textDescription = itemView.findViewById(R.id.tvDescription);
            textPrice = itemView.findViewById(R.id.tvPrice);

            // Tìm các View cho khách hàng
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            quantityControlsLayout = itemView.findViewById(R.id.quantity_controls);

            // Tìm các View cho admin
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            adminControlsLayout = itemView.findViewById(R.id.admin_buttons_layout);
        }
    }
}