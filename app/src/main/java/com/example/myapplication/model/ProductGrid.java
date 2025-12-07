package com.example.myapplication.model;

import java.io.Serializable;

public class ProductGrid implements Serializable {
    private String name;
    private String description;
    private double price;
    private int imageResId; // For local drawables
    private String imageUrl; // For remote URLs
    private String category;
    private boolean isFavorite;

    // Constructor for local images
    public ProductGrid(String name, String description, double price, int imageResId, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
        this.category = category;
        this.isFavorite = false;
    }

    // Constructor for remote images
    public ProductGrid(String name, String description, double price, String imageUrl, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.isFavorite = false;
    }

    // --- Getters and Setters ---

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
