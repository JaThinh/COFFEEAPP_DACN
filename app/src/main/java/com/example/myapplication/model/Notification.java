package com.example.myapplication.model;

public class Notification {
    private String title;
    private String message;
    private long timestamp;

    public Notification() {
        // Constructor mặc định cho Firebase
    }

    public Notification(String title, String message, long timestamp) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
