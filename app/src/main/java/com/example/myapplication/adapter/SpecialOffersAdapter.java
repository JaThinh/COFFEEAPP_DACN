package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;

public class SpecialOffersAdapter extends RecyclerView.Adapter<SpecialOffersAdapter.SpecialOfferViewHolder> {

    @NonNull
    @Override
    public SpecialOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_special_offers_layout, parent, false);
        return new SpecialOfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialOfferViewHolder holder, int position) {
        // No data to bind for now, as the layout is static
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.home_special_offers_layout;
    }

    @Override
    public int getItemCount() {
        return 1; // This section only appears once
    }

    static class SpecialOfferViewHolder extends RecyclerView.ViewHolder {
        public SpecialOfferViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
