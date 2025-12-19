package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.CartItem;

import java.text.DecimalFormat;
import java.util.List;

public class BillDetailAdapter extends RecyclerView.Adapter<BillDetailAdapter.BillViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###,### đ");

    public BillDetailAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bill_detail, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        // Hiển thị tên món (Ưu tiên getProductName, fallback getName)
        String productName = item.getProductName();
        if (productName == null) productName = item.getName();
        holder.tvProductName.setText(productName);

        // Hiển thị số lượng
        holder.tvQuantity.setText("x" + item.getQuantity());

        // Tính thành tiền (Đơn giá x Số lượng)
        // Ưu tiên getUnitPrice, fallback getProductPrice
        double price = item.getUnitPrice() > 0 ? item.getUnitPrice() : item.getProductPrice();
        double totalPrice = price * item.getQuantity();
        holder.tvTotalPrice.setText(decimalFormat.format(totalPrice));

        // Load ảnh sản phẩm an toàn
        // Ưu tiên getImageUrl, fallback placeholder
        String imageUrl = item.getImageUrl();
        // Nếu model CartItem cũ dùng int imageResId hoặc field khác, cần check thêm
        // Ở đây giả định dùng String URL hoặc resource ID trong model CartItem chuẩn

        if (imageUrl != null && !imageUrl.isEmpty()) {
             Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_coffee) // Placeholder khi đang load
                .error(R.drawable.ic_coffee)       // Placeholder khi lỗi
                .into(holder.imgProduct);
        } else if (item.getImageResId() != 0) {
             holder.imgProduct.setImageResource(item.getImageResId());
        } else {
             holder.imgProduct.setImageResource(R.drawable.ic_coffee);
        }
    }

    @Override
    public int getItemCount() {
        return cartItems == null ? 0 : cartItems.size();
    }

    public static class BillViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvQuantity, tvTotalPrice;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
        }
    }
}
