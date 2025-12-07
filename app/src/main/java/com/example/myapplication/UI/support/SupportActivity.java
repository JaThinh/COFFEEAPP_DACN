package com.example.myapplication.UI.support;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.MessageAdapter;
import com.example.myapplication.model.Message;

import java.util.ArrayList;
import java.util.List;

public class SupportActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editText;
    private Button buttonSend;
    private ImageButton btnBack;
    private MessageAdapter adapter;
    private List<Message> messages;

    // Sửa lại ID để tường minh hơn
    private final String userId = "support_user"; 
    private final String adminId = "support_admin";

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

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                TextView toolbarTitle = findViewById(R.id.toolbarTitle);
                if (toolbarTitle != null) {
                    toolbarTitle.setText("Hỗ trợ Khách hàng");
                }
            }
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }

        messages = new ArrayList<>();
        // SỬA LỖI: Sử dụng constructor mới của MessageAdapter
        adapter = new MessageAdapter(messages, userId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        buttonSend.setOnClickListener(v -> {
            String msg = editText.getText().toString().trim();
            if (!TextUtils.isEmpty(msg)) {
                sendMessage(msg);
                editText.setText("");
            }
        });
        
        addBotWelcomeMessage();
    }

    private void addBotWelcomeMessage() {
        // SỬA LỖI: Sử dụng constructor đầy đủ của Message
        Message welcomeMsg = new Message(adminId, userId, "Chào bạn, tôi có thể giúp gì cho bạn?", System.currentTimeMillis());
        messages.add(welcomeMsg);
        adapter.notifyItemInserted(messages.size() - 1);
    }

    private void sendMessage(String content) {
        // SỬA LỖI: Sử dụng constructor đầy đủ của Message
        Message userMessage = new Message(userId, adminId, content, System.currentTimeMillis());
        messages.add(userMessage);
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // SỬA LỖI: Sử dụng constructor đầy đủ của Message
            Message botResponse = new Message(adminId, userId, "Cảm ơn bạn đã liên hệ. Chúng tôi sẽ phản hồi trong thời gian sớm nhất.", System.currentTimeMillis());
            messages.add(botResponse);
            adapter.notifyItemInserted(messages.size() - 1);
            recyclerView.scrollToPosition(messages.size() - 1);
        }, 1000); 
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
