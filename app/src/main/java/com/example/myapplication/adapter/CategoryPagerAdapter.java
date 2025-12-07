package com.example.myapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.myapplication.UI.Home.ProductCategoryFragment; // Đã sửa lại đường dẫn import
import com.example.myapplication.model.Category;

import java.util.List;

public class CategoryPagerAdapter extends FragmentStateAdapter {

    private List<Category> categories;

    public CategoryPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Category> categories) {
        super(fragmentActivity);
        this.categories = categories;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance for the given position
        Category category = categories.get(position);
        return ProductCategoryFragment.newInstance(category.getId());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }
}
