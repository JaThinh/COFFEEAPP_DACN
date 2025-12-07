package com.example.myapplication.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;
    private String currentUserId;

    public MessageAdapter(List<Message> messages, String currentUserId)
    {
        this.messageList = messages;
        this.currentUserId = currentUserId;
    }
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewContent;
        TextView textViewTimestamp;
        LinearLayout bubbleLayout;

        public MessageViewHolder(View view) {
            super(view);
            textViewContent = view.findViewById(R.id.textViewContent);
            textViewTimestamp = view.findViewById(R.id.textViewTimestamp);
            bubbleLayout = view.findViewById(R.id.bubbleLayout);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message msg = messageList.get(position);
        boolean isSender = msg.getSenderId().equals(currentUserId);

        // Đặt nội dung tin nhắn
        holder.textViewContent.setText(msg.getContent());

        // Định dạng và đặt thời gian
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String time = sdf.format(new Date(msg.getTimestamp()));
        holder.textViewTimestamp.setText(time);

        // Điều chỉnh gravity của bubbleLayout trong messageRootLayout
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.bubbleLayout.getLayoutParams();
        if (isSender) {
            // Tin nhắn của người đang xem
            params.gravity = Gravity.END;
            holder.bubbleLayout.setBackgroundResource(R.drawable.bubble_user); // Bubble cho người gửi
            holder.textViewContent.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
            holder.textViewTimestamp.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
        } else
        {
            // Tin nhắn của người khác
            params.gravity = Gravity.START;
            holder.bubbleLayout.setBackgroundResource(R.drawable.bubble_bot); // Bubble cho người nhận
            holder.textViewContent.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.black));
            holder.textViewTimestamp.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
        }
        holder.bubbleLayout.setLayoutParams(params); // Áp dụng LayoutParams đã sửa đổi
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}