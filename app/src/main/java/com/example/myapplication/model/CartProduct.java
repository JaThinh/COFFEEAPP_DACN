package com.example.myapplication.model;

public class CartProduct {
    private int imageResId;
    private String name;
    private String options;
    private double price;
    private int quantity;

    public CartProduct(int imageResId, String name, String options, double price, int quantity) {
        this.imageResId = imageResId;
        this.name = name;
        this.options = options;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}