package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;

import java.util.List;

public class SearchSuggestionAdapter extends RecyclerView.Adapter<SearchSuggestionAdapter.SuggestionViewHolder> {

    private List<String> suggestions;
    private final OnSuggestionClickListener listener;

    public interface OnSuggestionClickListener {
        void onSuggestionClick(String suggestion);
    }

    public SearchSuggestionAdapter(List<String> suggestions, OnSuggestionClickListener listener) {
        this.suggestions = suggestions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_suggestion, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        String suggestion = suggestions.get(position);
        holder.bind(suggestion, listener);
    }

    @Override
    public int getItemCount() {
        return suggestions != null ? suggestions.size() : 0;
    }

    public void updateSuggestions(List<String> newSuggestions) {
        this.suggestions = newSuggestions;
        notifyDataSetChanged();
    }

    public static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        TextView suggestionText;

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            suggestionText = itemView.findViewById(R.id.tv_suggestion_text);
        }

        public void bind(final String suggestion, final OnSuggestionClickListener listener) {
            suggestionText.setText(suggestion);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSuggestionClick(suggestion);
                }
            });
        }
    }
}
