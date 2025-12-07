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
import com.example.myapplication.databinding.ItemCategoryBinding;
import com.example.myapplication.model.Category;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categoryList;
    private OnCategoryClickListener listener;

    // Interface bắt sự kiện click
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    // Interface bắt sự kiện cho chức năng quản lý (dùng cho Admin)
    public interface OnCategoryActionListener {
        void onEditCategory(Category category);
        void onDeleteCategory(Category category);
    }
    
    private OnCategoryActionListener actionListener;

    // Constructor cho User (chỉ cần click để lọc)
    public CategoryAdapter(Context context, List<Category> categoryList, OnCategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }
    
    // Constructor cho Admin (Cần edit/delete)
    public CategoryAdapter(Context context, List<Category> categoryList, OnCategoryActionListener actionListener) {
        this.context = context;
        this.categoryList = categoryList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private ItemCategoryBinding binding;

        public CategoryViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Category category) {
            binding.tvCategoryName.setText(category.getName());

            String imageUrl = category.getImageUrl();
            
            // Xử lý hiển thị ảnh
            if (imageUrl != null && imageUrl.length() > 100) {
                // Trường hợp Base64
                try {
                    byte[] decodedString = Base64.decode(imageUrl, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    if (decodedByte != null) {
                        binding.ivCategoryIcon.setImageBitmap(decodedByte);
                    } else {
                        binding.ivCategoryIcon.setImageResource(R.drawable.ic_category_placeholder);
                    }
                } catch (Exception e) {
                    binding.ivCategoryIcon.setImageResource(R.drawable.ic_category_placeholder);
                }
            } else {
                // Trường hợp Drawable resource name
                int resId = category.getResourceId(context);
                if (resId != 0) {
                    binding.ivCategoryIcon.setImageResource(resId);
                } else {
                    binding.ivCategoryIcon.setImageResource(R.drawable.ic_category_placeholder);
                }
            }

            // Xử lý sự kiện click
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                } else if (actionListener != null) {
                    actionListener.onEditCategory(category);
                }
            });
            
            // Nếu có nút xóa (chỉ hiện ở màn Admin)
            if (binding.btnDeleteCategory != null && actionListener != null) {
                binding.btnDeleteCategory.setOnClickListener(v -> actionListener.onDeleteCategory(category));
            }
        }
    }
}