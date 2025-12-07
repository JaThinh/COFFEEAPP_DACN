package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.Banner;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final List<Banner> bannerList;
    private final OnBannerListener listener;

    public interface OnBannerListener {
        void onDeleteClick(Banner banner);
    }

    public BannerAdapter(List<Banner> bannerList, OnBannerListener listener) {
        this.bannerList = bannerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner_admin, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Banner banner = bannerList.get(position);
        holder.bind(banner, listener);
    }

    @Override
    public int getItemCount() {
        return bannerList != null ? bannerList.size() : 0;
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBanner;
        ImageButton btnDelete;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.img_banner);
            btnDelete = itemView.findViewById(R.id.btn_delete_banner);
        }

        void bind(final Banner banner, final OnBannerListener listener) {
            if (banner == null || banner.getImageUrl() == null || banner.getImageUrl().isEmpty()) {
                return;
            }

            String imageUrl = banner.getImageUrl();
            Context context = itemView.getContext();

            if (imageUrl.startsWith("http")) {
                // Load từ URL (Firebase Storage)
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_coffee_placeholder)
                        .into(imgBanner);
            } else {
                // Load từ Drawable Resource (Offline)
                int resourceId = context.getResources().getIdentifier(imageUrl, "drawable", context.getPackageName());
                if (resourceId != 0) {
                    imgBanner.setImageResource(resourceId);
                } else {
                    // Fallback nếu không tìm thấy resource
                    imgBanner.setImageResource(R.drawable.ic_coffee_placeholder);
                }
            }

            btnDelete.setOnClickListener(v -> listener.onDeleteClick(banner));
        }
    }
}