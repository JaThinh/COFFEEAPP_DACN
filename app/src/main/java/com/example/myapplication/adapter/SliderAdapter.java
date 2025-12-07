package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.Slider;
import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private Context context;
    private List<Slider> sliderList;
    private final OnSliderClickListener listener;

    public interface OnSliderClickListener {
        void onSliderClick(Slider slider);
    }

    public SliderAdapter(Context context, List<Slider> sliderList, OnSliderClickListener listener) {
        this.context = context;
        this.sliderList = sliderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        Slider slider = sliderList.get(position);
        holder.bind(slider, listener);
    }

    @Override
    public int getItemCount() {
        return sliderList.size();
    }

    public static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_slider);
        }

        public void bind(final Slider slider, final OnSliderClickListener listener) {
            if (slider == null) return;

            // Sử dụng getImage() thay vì getResourceId()
            if (slider.getImage() != 0) {
                Glide.with(itemView.getContext())
                        .load(slider.getImage())
                        .into(imageView);
            } else {
                // Fallback nếu không có ảnh resource
                Glide.with(itemView.getContext())
                        .load(R.drawable.bg_placeholder)
                        .into(imageView);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onSliderClick(slider);
                    }
                }
            });
        }
    }
}