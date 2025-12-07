package com.example.myapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemAdminCategoryBinding;
import com.example.myapplication.model.Category;

import java.util.List;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.CategoryViewHolder> {

    private final Context context;
    private final List<Category> categoryList;
    private final OnCategoryActionListener listener;

    public interface OnCategoryActionListener {
        void onEdit(Category category);
        void onDelete(Category category);
    }

    public AdminCategoryAdapter(Context context, List<Category> categoryList, OnCategoryActionListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminCategoryBinding binding = ItemAdminCategoryBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(context, category, listener);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemAdminCategoryBinding binding;

        public CategoryViewHolder(ItemAdminCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Context context, final Category category, final OnCategoryActionListener listener) {
            binding.tvCategoryName.setText(category.getName());

            String imageUrl = category.getImageUrl();
            
            // Logic hiển thị ảnh chuẩn: Base64 -> Resource ID -> Fallback
            if (imageUrl != null && imageUrl.length() > 100) {
                // Xử lý Base64
                try {
                    byte[] decodedString = Base64.decode(imageUrl, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    if (decodedByte != null) {
                        binding.imgCategoryIcon.setImageBitmap(decodedByte);
                    } else {
                        binding.imgCategoryIcon.setImageResource(R.drawable.ic_category_placeholder);
                    }
                } catch (Exception e) {
                    binding.imgCategoryIcon.setImageResource(R.drawable.ic_category_placeholder);
                }
            } else {
                // Xử lý Resource Drawable (hoặc chuỗi ngắn khác)
                int resId = category.getResourceId(context);
                if (resId != 0) {
                    binding.imgCategoryIcon.setImageResource(resId);
                } else {
                    binding.imgCategoryIcon.setImageResource(R.drawable.ic_category_placeholder);
                }
            }

            binding.btnEdit.setOnClickListener(v -> listener.onEdit(category));
            binding.btnDelete.setOnClickListener(v -> listener.onDelete(category));
        }
    }
}