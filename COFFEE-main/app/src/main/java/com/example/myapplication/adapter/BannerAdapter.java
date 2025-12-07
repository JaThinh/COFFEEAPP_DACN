package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<Integer> bannerImages;
    private OnBannerClickListener onBannerClickListener;

    public BannerAdapter(List<Integer> bannerImages) {
        this.bannerImages = bannerImages;
    }

    // Interface để xử lý click banner
    public interface OnBannerClickListener {
        void onBannerClick(int position);
    }

    public void setOnBannerClickListener(OnBannerClickListener listener) {
        this.onBannerClickListener = listener;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        holder.bannerImageView.setImageResource(bannerImages.get(position));
        holder.itemView.setOnClickListener(v -> {
            if (onBannerClickListener != null) {
                onBannerClickListener.onBannerClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bannerImages.size();
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImageView;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImageView = itemView.findViewById(R.id.bannerImageView);
        }
    }
}