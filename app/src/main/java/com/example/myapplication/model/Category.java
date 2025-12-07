package com.example.myapplication.model;

import android.content.Context;

public class Category {
    private String id;
    private String name;
    private String image; // Lưu tên file drawable (vd: "ic_category_coffee") hoặc URL

    public Category() {
        // Constructor rỗng cho Firebase
    }

    public Category(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
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

    // Getter trả về chuỗi tên ảnh/URL
    public String getImageUrl() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Hàm helper tìm ID resource từ tên file ảnh (String).
     * Dùng để set ảnh cho ImageView từ folder drawable.
     */
    public int getResourceId(Context context) {
        if (image == null || image.isEmpty()) {
            return 0;
        }
        // Tìm ID trong thư mục drawable dựa trên tên file (biến image)
        return context.getResources().getIdentifier(image, "drawable", context.getPackageName());
    }
}