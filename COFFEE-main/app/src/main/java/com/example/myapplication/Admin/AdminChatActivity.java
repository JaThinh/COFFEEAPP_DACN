package com.example.myapplication.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button; // Đã đổi từ ImageButton sang Button
import android.widget.EditText;
import android.widget.ImageButton; // Giữ lại cho btnBack nếu có
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.MessageAdapter;
import com.example.myapplication.model.Conversation;
import com.example.myapplication.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editText;
    private Button buttonSend; // Đã sửa: Khai báo là Button
    private MessageAdapter adapter;
    private List<Message> messages;
    private ImageButton btnBack;
    private String customerId;
    private String adminId;
    private String customerName;

    private DatabaseReference chatRef, convoRef;

    // Admin
    private final String FIXED_ADMIN_ID = "Rvl5BRV9OghaOqrtWdAEzVHhlVp1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        // Cấu hình Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            // Ánh xạ TextView tiêu đề trong Toolbar
            TextView toolbarTitle = findViewById(R.id.toolbarTitle);
            if (toolbarTitle != null) {
                // Lấy tên khách hàng từ Intent
                customerName = getIntent().getStringExtra("userName");
                toolbarTitle.setText("Chat với: " + (customerName != null ? customerName : "Khách hàng"));
            } else {
                // Fallback nếu toolbarTitle không được tìm thấy
                customerName = getIntent().getStringExtra("userName");
                getSupportActionBar().setTitle("Chat với: " + (customerName != null ? customerName : "Khách hàng"));
            }

            // Ánh xạ và xử lý nút back
            btnBack = findViewById(R.id.btnBack);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> onBackPressed());
            }
        }

        // Lấy dữ liệu từ Intent customerId được truyền từ ChatWithCustomerActivity
        customerId = getIntent().getStringExtra("userId");

        adminId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : FIXED_ADMIN_ID;

        // Ánh xạ các View
        recyclerView = findViewById(R.id.recyclerViewMessages);
        editText = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages, adminId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Tạo chatId nhất quán
        String chatId = getChatId(customerId, adminId);
        chatRef = FirebaseDatabase.getInstance()
                .getReference("Chats")
                .child(chatId);

        convoRef = FirebaseDatabase.getInstance()
                .getReference("Conversations")
                .child(chatId);

        // Xử lý sự kiện gửi tin nhắn
        buttonSend.setOnClickListener(v -> {
            String msg = editText.getText().toString().trim();
            if (!TextUtils.isEmpty(msg)) {
                sendMessage(msg);
                editText.setText("");
            }
        });

        loadMessages();
    }

    // Xử lý nút back trên Toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendMessage(String text) {
        long timestamp = System.currentTimeMillis();
        Message message = new Message(adminId, customerId, text, timestamp);

        String key = chatRef.push().getKey();
        if (key != null) {
            chatRef.child(key).setValue(message)
                    .addOnSuccessListener(aVoid -> {
                        // Cập nhật Conversation sau khi gửi tin nhắn thành công
                        Conversation conversation = new Conversation(
                                customerId,
                                customerName != null ? customerName : "Khách hàng", // Đảm bảo tên khách hàng không null
                                text,
                                timestamp,
                                FIXED_ADMIN_ID // Sử dụng adminId cố định như trong SupportActivity
                        );
                        convoRef.setValue(conversation);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AdminChatActivity.this, "Lỗi gửi tin nhắn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Message msg = child.getValue(Message.class);
                    if (msg != null) {
                        messages.add(msg);
                    }
                }
                adapter.notifyDataSetChanged();
                if (messages.size() > 0) {
                    recyclerView.scrollToPosition(messages.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminChatActivity.this, "Lỗi tải tin nhắn: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getChatId(String u1, String u2) {
        return u1.compareTo(u2) < 0 ? u1 + "_" + u2 : u2 + "_" + u1;
    }
}