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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {

    private final Context context;
    private final List<CartItem> orderItemList;

    public OrderDetailAdapter(Context context, List<CartItem> orderItemList) {
        this.context = context;
        this.orderItemList = orderItemList;
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail_product, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        CartItem item = orderItemList.get(position);
        if (item == null) return;
        holder.bind(item, context);

        // Load image using Glide, with null/empty check
        String imageUrl = item.getImageUrl(); // FIXED: Use getImageUrl()
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder4) // Use a generic placeholder
                    .error(R.drawable.ic_placeholder4)
                    .into(holder.productImage);
        } else {
            Glide.with(context)
                    .load(R.drawable.ic_placeholder4) // Load placeholder if no image URL
                    .into(holder.productImage);
        }
    }

    @Override
    public int getItemCount() {
        return orderItemList != null ? orderItemList.size() : 0;
    }

    public static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, quantity;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.iv_product_image);
            productName = itemView.findViewById(R.id.tv_product_name);
            productPrice = itemView.findViewById(R.id.tv_product_price);
            quantity = itemView.findViewById(R.id.tv_quantity);
        }

        public void bind(CartItem item, Context context) {
            productName.setText(item.getProductName());

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            productPrice.setText(formatter.format(item.getProductPrice()));
            quantity.setText(context.getString(R.string.quantity_display, item.getQuantity()));
        }
    }
}
