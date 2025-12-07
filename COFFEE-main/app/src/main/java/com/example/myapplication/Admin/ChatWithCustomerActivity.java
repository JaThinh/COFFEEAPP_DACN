package com.example.myapplication.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ConversationAdapter;
import com.example.myapplication.model.Conversation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class ChatWithCustomerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ConversationAdapter adapter;
    private List<Conversation> conversations;
    private TextView tvEmptyConversations;

    // Admin ID
    private final String FIXED_ADMIN_ID = "Rvl5BRV9OghaOqrtWdAEzVHhlVp1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_customer);

        // Cấu hình Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarChatList);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Danh sách cuộc trò chuyện");
        }

        recyclerView = findViewById(R.id.recyclerViewConversations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tvEmptyConversations = findViewById(R.id.tvEmptyConversations);

        conversations = new ArrayList<>();
        // Khởi tạo adapter với listener để xử lý click
        adapter = new ConversationAdapter(conversations, conversation -> {
            // Khi admin click vào một cuộc trò chuyện, chuyển đến AdminChatActivity
            // Truyền userId và userName của khách hàng để hiển thị trong khung chat
            Intent intent = new Intent(ChatWithCustomerActivity.this, AdminChatActivity.class);
            intent.putExtra("userId", conversation.getUserId());
            intent.putExtra("userName", conversation.getUserName());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        loadConversations();
    }

    // Xử lý sự kiện click cho nút quay lại trên Toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadConversations() {
        tvEmptyConversations.setVisibility(View.GONE); // Ẩn thông báo rỗng ban đầu

        // Lấy adminId hiện tại từ Firebase Auth
        String currentAdminId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : FIXED_ADMIN_ID;


        if (currentAdminId == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID admin.", Toast.LENGTH_SHORT).show();
            tvEmptyConversations.setText("Lỗi: Không tìm thấy thông tin admin.");
            tvEmptyConversations.setVisibility(View.VISIBLE);
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Conversations");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                conversations.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Conversation convo = child.getValue(Conversation.class);
                    // Lọc các cuộc trò chuyện mà admin hiện tại là một phần của nó
                    if (convo != null) {
                        if (convo.getAdminId() != null && convo.getAdminId().equals(currentAdminId)) {
                            conversations.add(convo);
                        }
                    }
                }

                adapter.notifyDataSetChanged();

                // Hiển thị thông báo khi không có cuộc trò chuyện nào
                if (conversations.isEmpty()) {
                    tvEmptyConversations.setVisibility(View.VISIBLE);
                    tvEmptyConversations.setText("Chưa có cuộc trò chuyện nào.");
                } else {
                    tvEmptyConversations.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatWithCustomerActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_LONG).show();
                tvEmptyConversations.setText("Không thể tải cuộc trò chuyện. Vui lòng thử lại sau.");
                tvEmptyConversations.setVisibility(View.VISIBLE);
            }
        });
    }
}