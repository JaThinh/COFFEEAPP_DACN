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

public class AdminBannerAdapter extends RecyclerView.Adapter<AdminBannerAdapter.BannerViewHolder> {

    private List<Banner> mListBanner;
    private OnDeleteBannerListener listener;

    public interface OnDeleteBannerListener {
        void onDeleteClick(Banner banner);
    }

    public AdminBannerAdapter(List<Banner> mListBanner, OnDeleteBannerListener listener) {
        this.mListBanner = mListBanner;
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
        Banner banner = mListBanner.get(position);
        if (banner == null) return;

        Context context = holder.itemView.getContext();
        
        // Load image (support both drawable resource name and URL)
        int resourceId = context.getResources().getIdentifier(banner.getImageUrl(), "drawable", context.getPackageName());
        if (resourceId != 0) {
            Glide.with(context)
                    .load(resourceId)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(holder.imgBanner);
        } else {
             Glide.with(context)
                    .load(banner.getImageUrl())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(holder.imgBanner);
        }

        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(banner));
    }

    @Override
    public int getItemCount() {
        if (mListBanner != null) {
            return mListBanner.size();
        }
        return 0;
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgBanner;
        private ImageButton btnDelete;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.img_banner);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
