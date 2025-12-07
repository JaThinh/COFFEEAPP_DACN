package com.example.myapplication.model;

import java.io.Serializable;

public class Message implements Serializable {
    private String senderId;
    private String receiverId;
    private String content;
    private long timestamp;
    
    // Thêm các trường mới cho tính năng Product Card
    private boolean isProduct; // Đánh dấu đây là tin nhắn chứa sản phẩm
    private Product product;   // Lưu thông tin sản phẩm

    public Message() {}

    public Message(String senderId, String receiverId, String content, long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
        this.isProduct = false;
    }

    // Constructor cho tin nhắn text thông thường
    public Message(String content, boolean isFromUser) {
        this.content = content;
        this.senderId = isFromUser ? "local_user" : "gemini_bot";
        this.receiverId = isFromUser ? "gemini_bot" : "local_user";
        this.timestamp = System.currentTimeMillis();
        this.isProduct = false;
    }

    // Constructor cho tin nhắn sản phẩm
    public Message(Product product) {
        this.content = ""; // Nội dung text có thể để trống hoặc mô tả ngắn
        this.senderId = "gemini_bot"; // Mặc định là bot gửi sản phẩm
        this.receiverId = "local_user";
        this.timestamp = System.currentTimeMillis();
        this.isProduct = true;
        this.product = product;
    }

    // Getters
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public boolean isProduct() { return isProduct; }
    public Product getProduct() { return product; }

    // Setter
    public void setContent(String content) {
        this.content = content;
    }
    
    public void setProduct(Product product) {
        this.product = product;
        this.isProduct = true;
    }

    public boolean isUser(String currentUserId) {
        return this.senderId != null && this.senderId.equals(currentUserId);
    }
}
