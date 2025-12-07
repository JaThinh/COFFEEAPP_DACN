package com.example.myapplication.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Category;

import java.util.List;

public class CategoryChipAdapter extends RecyclerView.Adapter<CategoryChipAdapter.CategoryChipViewHolder> {

    private List<Category> categoryList;
    private final OnCategoryClickListener listener;
    private int selectedPosition = 0; // Default to first item selected ("Tất cả")

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryChipAdapter(List<Category> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    public void updateCategories(List<Category> newCategories) {
        this.categoryList.clear();
        this.categoryList.addAll(newCategories);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryChipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryChipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryChipViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(category, position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category);
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    int previousPosition = selectedPosition;
                    selectedPosition = holder.getAdapterPosition();
                    notifyItemChanged(previousPosition);
                    notifyItemChanged(selectedPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    static class CategoryChipViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryIcon;
        TextView categoryName;
        Context context;

        public CategoryChipViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            categoryIcon = itemView.findViewById(R.id.iv_category_icon);
            categoryName = itemView.findViewById(R.id.tv_category_name);
        }

        void bind(Category category, boolean isSelected) {
            categoryName.setText(category.getName());

            String imageUrl = category.getImageUrl();
            
            // 1. Ưu tiên Base64
            if (imageUrl != null && imageUrl.length() > 100) {
                try {
                    byte[] decodedString = Base64.decode(imageUrl, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    if (decodedByte != null) {
                        categoryIcon.setImageBitmap(decodedByte);
                    } else {
                        categoryIcon.setImageResource(R.drawable.ic_category_placeholder);
                    }
                } catch (Exception e) {
                    categoryIcon.setImageResource(R.drawable.ic_category_placeholder);
                }
            } 
            // 2. Sau đó đến Resource Drawable
            else {
                int resId = category.getResourceId(context); // Sửa lỗi: Thêm tham số context
                if (resId != 0) {
                    categoryIcon.setImageResource(resId);
                } else {
                    categoryIcon.setImageResource(R.drawable.ic_category_placeholder); 
                }
            }

            if (isSelected) {
                itemView.setBackground(null);
                categoryName.setTextColor(ContextCompat.getColor(context, R.color.coffee_brown));
                categoryIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.coffee_brown)));
            } else {
                itemView.setBackground(null);
                categoryName.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
                categoryIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.text_secondary)));
            }
        }
    }
}