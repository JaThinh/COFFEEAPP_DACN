package com.example.myapplication.model;

import java.io.Serializable;
import java.util.List;

// This model is designed for Firebase Realtime Database.
// It now uses a list of FirebaseCartItem, which is a simple POJO.
public class FirebaseOrder implements Serializable {
    private String orderId;
    private String userId;
    private long timestamp;
    private List<FirebaseCartItem> items; // Changed from CartItem to FirebaseCartItem
    private double totalPrice;
    private String customerName;
    private String phoneNumber;
    private String shippingAddress;
    private String paymentMethod;
    private String status;

    public FirebaseOrder() {
        // Default constructor for Firebase
    }

    public FirebaseOrder(String orderId, String userId, long timestamp, List<FirebaseCartItem> items, double totalPrice, String customerName, String phoneNumber, String shippingAddress, String paymentMethod, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.items = items;
        this.totalPrice = totalPrice;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public List<FirebaseCartItem> getItems() { return items; } // Return type changed
    public void setItems(List<FirebaseCartItem> items) { this.items = items; } // Parameter type changed
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
