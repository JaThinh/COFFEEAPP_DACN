package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;

public class HeaderAdapter extends RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder> {

    public interface HeaderListener {
        void onSearchQuery(String query);
    }

    private HeaderListener listener;
    private String userName = "User";

    public HeaderAdapter(HeaderListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_header_layout, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderViewHolder holder, int position) {
        holder.bind(userName, listener);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.home_header_layout;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public void updateUserName(String name) {
        this.userName = name;
        notifyItemChanged(0);
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        SearchView searchView;
        TextView tvWelcomeMessage;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            searchView = itemView.findViewById(R.id.search_view);
            tvWelcomeMessage = itemView.findViewById(R.id.tv_welcome_message);
        }

        public void bind(String userName, HeaderListener listener) {
            tvWelcomeMessage.setText("Chào buổi sáng, " + userName + "!");

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (listener != null) {
                        listener.onSearchQuery(query);
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }
            });
        }
    }
}
