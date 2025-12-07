package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.Conversation;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onClick(Conversation conversation);
    }

    private List<Conversation> conversationList;
    private OnItemClickListener listener;

    public ConversationAdapter(List<Conversation> list, OnItemClickListener listener) {
        this.conversationList = list;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewMessage;
        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewUserName);
            textViewMessage = itemView.findViewById(R.id.textViewLastMessage);
        }

        public void bind(Conversation convo, OnItemClickListener listener) {
            textViewName.setText(convo.getUserName());
            textViewMessage.setText(convo.getLastMessage());
            itemView.setOnClickListener(v -> listener.onClick(convo));
        }
    }

    @NonNull
    @Override
    public ConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationAdapter.ViewHolder holder, int position) {
        holder.bind(conversationList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }
}
