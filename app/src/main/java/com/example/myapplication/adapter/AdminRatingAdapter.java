package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemRatingBinding;
import com.example.myapplication.model.Rating;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminRatingAdapter extends RecyclerView.Adapter<AdminRatingAdapter.RatingViewHolder> {

    private final List<Rating> ratingList;
    private final Context context;

    public AdminRatingAdapter(Context context, List<Rating> ratingList) {
        this.context = context;
        this.ratingList = ratingList;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRatingBinding binding = ItemRatingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RatingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        Rating rating = ratingList.get(position);
        holder.bind(rating);
    }

    @Override
    public int getItemCount() {
        return ratingList != null ? ratingList.size() : 0;
    }

    static class RatingViewHolder extends RecyclerView.ViewHolder {
        private final ItemRatingBinding binding;

        public RatingViewHolder(@NonNull ItemRatingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Rating rating) {
            if (rating == null) return;

            binding.tvUserName.setText(rating.getUserName());

            // ** THE FIX: Use the correct method 'getStars()' from the Rating model **
            binding.ratingBarItem.setRating(rating.getStars());

            binding.tvComment.setText(rating.getComment());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            binding.tvRatingDate.setText(sdf.format(new Date(rating.getTimestamp())));
        }
    }
}
