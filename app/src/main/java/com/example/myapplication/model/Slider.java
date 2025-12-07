package com.example.myapplication.model;

public class Slider {
    private int imageResId; // Local drawable
    private String imageUrl; // Firebase/Remote URL
    private String title;

    public Slider() {
    }

    // Constructor 1: Local Resource
    public Slider(int imageResId, String title) {
        this.imageResId = imageResId;
        this.title = title;
    }
    
    // Constructor 2: Remote URL
    public Slider(String imageUrl, String title) {
        this.imageUrl = imageUrl;
        this.title = title;
    }
    
    // Constructor 3: Chỉ URL (nếu không cần title)
    public Slider(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // --- GETTERS & SETTERS ---
    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    // Hỗ trợ tương thích ngược cho code cũ dùng getImage() trả về int
    public int getImage() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}