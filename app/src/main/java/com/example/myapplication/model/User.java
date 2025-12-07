package com.example.myapplication.model;

import com.google.firebase.database.PropertyName;

public class User {
    private String id;
    private String name;
    private String email;
    private String imageUrl;
    private String password;
    private String phone;
    private String address;
    private String role;
    private String fullName; // For compatibility with Firebase

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public User(String email, String password, String name, String phone, String address, String role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter for 'name' is now robust, checks both 'name' and 'fullName'
    public String getName() {
        if (name != null && !name.isEmpty()) {
            return name;
        }
        if (fullName != null && !fullName.isEmpty()) {
            return fullName;
        }
        return ""; // Return empty string to prevent null pointer issues
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter/Setter for 'fullName' to fix the ClassMapper warning
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
