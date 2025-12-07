package com.example.myapplication.UI.support;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.adapter.MessageAdapter;
import com.example.myapplication.model.Message;
import com.example.myapplication.model.Product;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatbotActivity extends AppCompatActivity {

    private static final String KEY_MESSAGES = "key_messages";
    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ArrayList<Message> messageList;
    private MessageAdapter messageAdapter;

    private ChatFutures chat;
    private final Executor backgroundExecutor = Executors.newSingleThreadExecutor();

    // Biến lưu trữ menu và lịch sử để gửi cho AI
    private String menuContext = "";
    private String userHistoryContext = "";
    private boolean isFirstMessageToSend = true;
    
    // Danh sách toàn bộ sản phẩm để tra cứu khi bot gợi ý
    private List<Product> allProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        if (savedInstanceState != null && savedInstanceState.getSerializable(KEY_MESSAGES) != null) {
            //noinspection unchecked
            messageList = (ArrayList<Message>) savedInstanceState.getSerializable(KEY_MESSAGES);
        } else {
            messageList = new ArrayList<>();
        }

        String localUserId = "local_user";
        messageAdapter = new MessageAdapter(messageList, localUserId);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(messageAdapter);

        // Bước 1: Lấy dữ liệu (Menu + Lịch sử) -> Sau đó Init AI
        loadDataAndInitAI();

        sendButton.setOnClickListener(v -> {
            String messageText = messageEditText.getText().toString().trim();
            if (!messageText.isEmpty()) {
                addMessage(new Message(messageText, true));
                messageEditText.setText("");
                sendMessageToChatbot(messageText);
            } else {
                Toast.makeText(ChatbotActivity.this, "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_MESSAGES, messageList);
    }

    private void loadDataAndInitAI() {
        // 1. Lấy Menu trước - Sửa nhánh thành "products"
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allProducts.clear();
                StringBuilder sb = new StringBuilder();
                int count = 0;
                for (DataSnapshot data : snapshot.getChildren()) {
                    Product product = data.getValue(Product.class);
                    if (product != null) {
                        allProducts.add(product); // Lưu vào danh sách để tra cứu sau này
                        count++;
                        sb.append("- ").append(product.getName())
                          .append(" (").append(formatPrice(product.getPrice())).append(" VND)");
                        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
                            sb.append(": ").append(product.getDescription());
                        }
                        sb.append("\n");
                    }
                }
                menuContext = sb.toString();
                Log.d("ChatbotActivity", "Loaded Menu: " + count + " items.");

                // 2. Sau khi có menu, lấy tiếp Lịch sử ăn uống (nếu đã đăng nhập)
                fetchUserHistoryAndStartAI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatbotActivity", "Menu error: " + error.getMessage());
                fetchUserHistoryAndStartAI(); // Vẫn tiếp tục dù lỗi menu
            }
        });
    }

    private void fetchUserHistoryAndStartAI() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            initGeminiClient(); // Khách vãng lai -> Start luôn
            return;
        }

        // Sửa nhánh thành "users" và "order_history"
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("order_history");
        historyRef.limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Các món khách này đã từng gọi gần đây: ");
                    List<String> dishes = new ArrayList<>();
                    for (DataSnapshot item : snapshot.getChildren()) {
                        String dishName = item.getValue(String.class);
                        if (dishName != null) dishes.add(dishName);
                    }
                    sb.append(dishes.toString());
                    userHistoryContext = sb.toString();
                    Log.d("ChatbotActivity", "Loaded History: " + userHistoryContext);
                }
                initGeminiClient(); // Có hay không cũng start AI
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatbotActivity", "History error: " + error.getMessage());
                initGeminiClient();
            }
        });
    }

    private String formatPrice(double price) {
        return String.format("%,.0f", price);
    }

    private void initGeminiClient() {
        backgroundExecutor.execute(() -> {
            try {
                Log.d("ChatbotActivity", "Bắt đầu khởi tạo GenerativeModel...");
                String apiKey = "AIzaSyCpFaYmtDIslM2f4fkmoG-bJbHZUrQYalQ"; 
                
                if (apiKey == null || apiKey.isEmpty() || apiKey.equals("null")) {
                     runOnUiThread(() -> Toast.makeText(this, "API Key chưa được cấu hình", Toast.LENGTH_LONG).show());
                     return;
                }

                GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", apiKey, null, null);
                GenerativeModelFutures model = GenerativeModelFutures.from(gm);
                chat = model.startChat();
                Log.d("ChatbotActivity", "Khởi tạo Chatbot thành công.");

                if (messageList.isEmpty()) {
                    runOnUiThread(() -> {
                        String greeting = "Xin chào! Mình là trợ lý ảo Aura Cafe. Mình có thể giúp gì cho bạn?";
                        if (!userHistoryContext.isEmpty()) {
                            greeting += " (Mình thấy bạn hay ghé quán, để mình gợi ý món hợp gu bạn nhé!)";
                        }
                        addMessage(new Message(greeting, false));
                    });
                }
            } catch (Throwable t) {
                Log.e("ChatbotActivity", "Lỗi khởi tạo Chatbot: " + t.getMessage(), t);
            }
        });
    }

    private void addMessage(Message message) {
        messageList.add(message);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        chatRecyclerView.scrollToPosition(messageList.size() - 1);
    }

    private void sendMessageToChatbot(String userMessage) {
        if (chat == null) {
            addMessage(new Message("Chatbot đang khởi động...", false));
            return;
        }

        setUiEnabled(false);
        Message loadingMessage = new Message("...", false);
        addMessage(loadingMessage);
        int botMessageIndex = messageList.size() - 1;

        Executor mainExecutor = ContextCompat.getMainExecutor(this);

        String contentToSend = userMessage;
        
        // --- KỸ THUẬT PROMPT KẾT HỢP: MENU + LỊCH SỬ ---
        if (isFirstMessageToSend) {
            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append("Đóng vai nhân viên Aura Cafe thân thiện.\n");
            
            if (!menuContext.isEmpty()) {
                promptBuilder.append("MENU QUÁN:\n").append(menuContext).append("\n");
            }
            
            if (!userHistoryContext.isEmpty()) {
                promptBuilder.append("THÔNG TIN KHÁCH HÀNG:\n").append(userHistoryContext).append("\n");
                promptBuilder.append("Gợi ý: Hãy dựa vào lịch sử ăn uống trên để tư vấn món phù hợp trong Menu. Ví dụ khách thích ngọt thì tư vấn bánh/trà sữa.\n");
            }

            promptBuilder.append("Yêu cầu: Trả lời ngắn gọn, tập trung bán hàng.\n");
            promptBuilder.append("Lưu ý quan trọng: Khi nhắc đến tên món ăn, hãy viết chính xác tên như trong menu.\n");
            promptBuilder.append("Khách hỏi: ").append(userMessage);
            
            contentToSend = promptBuilder.toString();
            isFirstMessageToSend = false;
            Log.d("ChatbotActivity", "Full Prompt sent to AI.");
        }
        // ------------------------------------------------

        Content userContent = new Content.Builder().addText(contentToSend).build();
        ListenableFuture<GenerateContentResponse> responseTask = chat.sendMessage(userContent);

        Futures.addCallback(responseTask, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String botResponseText = (result.getText() != null) ? result.getText() : "Lỗi phản hồi.";
                
                // 1. Cập nhật tin nhắn text của bot
                loadingMessage.setContent(botResponseText);
                messageAdapter.notifyItemChanged(botMessageIndex);
                
                // 2. Kiểm tra và hiển thị Product Card nếu có tên món ăn trong câu trả lời
                checkForProductInResponse(botResponseText);

                setUiEnabled(true);
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                Log.e("ChatbotActivity", "Lỗi API: " + t.getMessage(), t);
                loadingMessage.setContent("Lỗi kết nối AI.");
                messageAdapter.notifyItemChanged(botMessageIndex);
                setUiEnabled(true);
            }
        }, mainExecutor);
    }
    
    private void checkForProductInResponse(String responseText) {
        if (allProducts == null || allProducts.isEmpty()) return;

        String normalizedResponse = responseText.toLowerCase();
        Product foundProduct = null;

        // Ưu tiên tìm tên chính xác và dài nhất trước để tránh nhầm lẫn (ví dụ "Cà phê" vs "Cà phê sữa")
        // Tuy nhiên logic đơn giản ở đây là duyệt qua list
        for (Product product : allProducts) {
            if (normalizedResponse.contains(product.getName().toLowerCase())) {
                foundProduct = product;
                break; // Chỉ hiển thị 1 món đầu tiên tìm thấy để tránh spam
            }
        }

        if (foundProduct != null) {
            // Tạo tin nhắn dạng Product
            Message productMessage = new Message(foundProduct);
            addMessage(productMessage);
        }
    }

    private void setUiEnabled(boolean enabled) {
        sendButton.setEnabled(enabled);
        messageEditText.setEnabled(enabled);
    }
}
