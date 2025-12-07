package com.example.myapplication.model;

import java.io.Serializable;

public class Product implements Serializable {
    private String id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private int imageResId; // New field for drawable resource ID
    private String categoryId;
    private String categoryName;
    private boolean isFavorite;
    private double rating;
    private long ratingCount;
    private int soldCount;
    
    // Thêm biến soldQuantity nếu cần thiết cho Firebase mapping hoặc logic khác
    // Tuy nhiên để tránh trùng lặp, ta sẽ dùng soldCount làm biến chính

    public Product() {
    }

    // Constructor for Firebase data (with imageUrl)
    public Product(String name, double price, String imageUrl, String categoryId, String description) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.description = description;
        this.imageResId = 0; // Default value
    }
    
    // Constructor for local/mock data (with imageResId)
    public Product(String name, String description, double price, int imageResId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
        this.imageUrl = null; // Ensure imageUrl is null when using local resource
    }

    // New constructor for local/mock data with categoryId
    public Product(String name, String description, double price, int imageResId, String categoryId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
        this.categoryId = categoryId;
        this.imageUrl = null;
    }

    public Product(String id, String name, String description, double price, String imageUrl, String categoryId, String categoryName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public long getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(long ratingCount) {
        this.ratingCount = ratingCount;
    }

    public int getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }

    public void setAverageRating(double newAverage) {
        this.rating = newAverage;
    }

    // --- BỔ SUNG CÁC GETTER/SETTER CHO TƯƠNG THÍCH CODE CŨ/MỚI ---
    public int getSoldQuantity() {
        return soldCount;
    }

    public void setSoldQuantity(int soldQuantity) {
        this.soldCount = soldQuantity;
    }
}
