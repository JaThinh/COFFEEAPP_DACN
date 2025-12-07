package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Category;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {

    private final List<Category> categoryList;
    private final CategoryChipAdapter.OnCategoryClickListener listener;

    public CategoriesAdapter(List<Category> categoryList, CategoryChipAdapter.OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_categories_layout, parent, false);
        return new CategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {
        CategoryChipAdapter chipAdapter = new CategoryChipAdapter(categoryList, listener);
        holder.recyclerViewCategories.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerViewCategories.setAdapter(chipAdapter);
    }

    // --- FIX: Added getItemViewType to work with the robust SpanSizeLookup ---
    @Override
    public int getItemViewType(int position) {
        return R.layout.home_categories_layout;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    static class CategoriesViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerViewCategories;

        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerViewCategories = itemView.findViewById(R.id.recycler_view_categories);
        }
    }
}
