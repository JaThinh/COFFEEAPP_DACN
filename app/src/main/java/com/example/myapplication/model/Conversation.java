package com.example.myapplication.model;

public class Conversation {
    private String userId;
    private String userName;
    private String lastMessage;
    private long timestamp;
    private String adminId; // Đã thêm trường adminId

    public Conversation() {
        // Required for Firebase
    }

    // Constructor mới bao gồm adminId
    public Conversation(String userId, String userName, String lastMessage, long timestamp, String adminId) {
        this.userId = userId;
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.adminId = adminId; // Khởi tạo adminId
    }

    // --- Getters ---
    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getAdminId() { // Getter cho adminId
        return adminId;
    }

    // --- Setters ---
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setAdminId(String adminId) { // Setter cho adminId
        this.adminId = adminId;
    }
}