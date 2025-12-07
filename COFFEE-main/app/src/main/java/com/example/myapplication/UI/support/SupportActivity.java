package com.example.myapplication.UI.support;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem; // Import MenuItem
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView; // Import TextView
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SupportActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editText;
    private Button buttonSend;
    private ImageButton btnBack;
    private MessageAdapter adapter;
    private List<Message> messages;

    private DatabaseReference chatRef;
    private DatabaseReference conversationRef;
    private String userId;
    private String userName = "Khách hàng";
    private final String adminId = "Rvl5BRV9OghaOqrtWdAEzVHhlVp1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        // Init View
        Toolbar toolbar = findViewById(R.id.toolbar);
        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerViewMessages);
        editText = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        // Firebase User
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            // Lấy userName từ profile của Firebase
            userName = currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty() ?
                    currentUser.getDisplayName() : "Khách hàng (" + userId.substring(0, 4) + "...)";
        } else {
            Toast.makeText(this, "Bạn chưa đăng nhập.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tạo chatId nhất quán giữa admin và user
        String chatId = getChatId(userId, adminId);
        chatRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatId);
        conversationRef = FirebaseDatabase.getInstance().getReference("Conversations").child(chatId);

        // Cấu hình Toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);

                // Đặt tiêu đề tùy chỉnh
                TextView toolbarTitle = findViewById(R.id.toolbarTitle);
                if (toolbarTitle != null) {
                    toolbarTitle.setText("Hỗ trợ Khách hàng");
                } else {
                    getSupportActionBar().setTitle("Hỗ trợ Khách hàng");
                }
            }
        }

        // Xử lý nút back trên Toolbar
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }

        // Cấu hình RecyclerView
        messages = new ArrayList<>();
        // Truyền userId vào MessageAdapter
        adapter = new MessageAdapter(messages, userId); // userId là ID của
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Load tin nhắn realtime
        loadMessages();

        // Xử lý sự kiện gửi tin nhắn
        buttonSend.setOnClickListener(v -> {
            String msg = editText.getText().toString().trim();
            if (!TextUtils.isEmpty(msg)) {
                sendMessage(msg);
                editText.setText("");
            }
        });
    }

    private void sendMessage(String content) {
        long timestamp = System.currentTimeMillis();
        // Tin nhắn từ khách hàng gửi cho admin
        Message message = new Message(userId, adminId, content, timestamp);

        String key = chatRef.push().getKey();
        if (key != null) {
            chatRef.child(key).setValue(message)
                    .addOnSuccessListener(aVoid -> {
                        Conversation conversation = new Conversation(userId, userName, content, timestamp, adminId);
                        conversationRef.setValue(conversation)
                                .addOnFailureListener(e -> {
                                    Toast.makeText(SupportActivity.this, "Lỗi cập nhật hội thoại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SupportActivity.this, "Lỗi gửi tin nhắn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Message message = child.getValue(Message.class);
                    if (message != null) {
                        messages.add(message);
                    }
                }
                adapter.notifyDataSetChanged();
                if (messages.size() > 0) {
                    recyclerView.scrollToPosition(messages.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SupportActivity.this, "Lỗi tải tin nhắn: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm tạo Chat ID
    private String getChatId(String user1, String user2) {
        return user1.compareTo(user2) < 0 ? user1 + "_" + user2 : user2 + "_" + user1;
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
}