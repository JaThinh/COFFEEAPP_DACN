package com.example.myapplication.model;

// This is a simple POJO class designed specifically for Firebase serialization.
public class FirebaseCartItem {
    private String productId;
    private String productName;
    private double productPrice;
    private String productImageUrl;
    private int quantity;
    private String size;
    private String sugar;
    private String ice;
    private String toppings;
    private String category; // ** THE FIX: Add category field **

    // Required empty public constructor for Firebase
    public FirebaseCartItem() {}

    // Getters and Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public double getProductPrice() { return productPrice; }
    public void setProductPrice(double productPrice) { this.productPrice = productPrice; }
    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getSugar() { return sugar; }
    public void setSugar(String sugar) { this.sugar = sugar; }
    public String getIce() { return ice; }
    public void setIce(String ice) { this.ice = ice; }
    public String getToppings() { return toppings; }
    public void setToppings(String toppings) { this.toppings = toppings; }
    public String getCategory() { return category; } // ** THE FIX: Add getter **
    public void setCategory(String category) { this.category = category; } // ** THE FIX: Add setter **
}
