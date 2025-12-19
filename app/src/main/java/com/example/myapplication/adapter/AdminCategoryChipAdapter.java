package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Category;
import com.google.android.material.chip.Chip;

import java.util.List;

public class AdminCategoryChipAdapter extends RecyclerView.Adapter<AdminCategoryChipAdapter.ChipViewHolder> {

    private List<Category> categoryList;
    private Context context;
    private OnCategoryChipClickListener listener;
    private int selectedPosition = 0; // Default: "Tất cả"

    public interface OnCategoryChipClickListener {
        void onCategoryClick(Category category);
    }

    public AdminCategoryChipAdapter(Context context, List<Category> categoryList, OnCategoryChipClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_chip, parent, false);
        return new ChipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChipViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.chip.setText(category.getName());
        
        holder.chip.setChecked(position == selectedPosition);

        holder.chip.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
            
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    public static class ChipViewHolder extends RecyclerView.ViewHolder {
        Chip chip;

        public ChipViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = (Chip) itemView;
        }
    }
}
