package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.R;
import com.example.myapplication.model.Banner;

import java.util.List;

public class ViewPagerImageAdapter extends RecyclerView.Adapter<ViewPagerImageAdapter.ImageViewHolder> {

    private final List<Banner> bannerList;

    public ViewPagerImageAdapter(List<Banner> bannerList) {
        this.bannerList = bannerList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Banner banner = bannerList.get(position);
        String imageUrl = (banner != null) ? banner.getImageUrl() : null; // Safely get imageUrl

        // --- FIX: Added null/empty check for imageUrl before loading with Glide ---
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .placeholder(R.drawable.bg_placeholder) // Use custom placeholder
                    .error(R.drawable.bg_placeholder)       // Use custom error image
                    .into(holder.bannerImageView);
        } else {
            holder.bannerImageView.setImageResource(R.drawable.bg_placeholder); // Set placeholder if URL is null or empty
        }
    }

    @Override
    public int getItemCount() {
        return bannerList != null ? bannerList.size() : 0;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImageView = itemView.findViewById(R.id.iv_banner_image);
        }
    }
}
