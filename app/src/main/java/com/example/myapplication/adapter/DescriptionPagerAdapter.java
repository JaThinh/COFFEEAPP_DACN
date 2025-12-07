package com.example.myapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.UI.Detail.tabs.ProductNutritionFragment;
import com.example.myapplication.UI.Detail.tabs.ProductStoryFragment;

public class DescriptionPagerAdapter extends FragmentStateAdapter {

    public DescriptionPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new ProductNutritionFragment();
            case 0:
            default:
                return new ProductStoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // We have two tabs: Story and Nutrition
    }
}
