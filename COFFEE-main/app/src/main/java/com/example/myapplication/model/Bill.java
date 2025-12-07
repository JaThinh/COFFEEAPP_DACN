package com.example.myapplication.model;

import com.example.myapplication.Item.CartItem;

import java.io.Serializable;
import java.util.List;

public class Bill implements Serializable {
    private String userName;
    private String timestamp;
    private double total;
    private List<CartItem> items;
    private String billId;
    private String paymentMethod;


    // Constructor mặc định cho Firebase
    public Bill() {
    }

    // CẬP NHẬT CONSTRUCTOR ĐỂ BAO GỒM paymentMethod
    public Bill(String userName, String timestamp, double total, List<CartItem> items, String paymentMethod,String voucher) {
        this.userName = userName;
        this.timestamp = timestamp;
        this.total = total;
        this.items = items;
        this.paymentMethod = paymentMethod;

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getTotal() {
        return total;
    }
    public void setTotal(double total) {
        this.total = total;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }


    // THÊM GETTER VÀ SETTER CHO paymentMethod
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}