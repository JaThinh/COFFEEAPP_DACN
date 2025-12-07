package com.example.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

public class CartItem implements Parcelable, Serializable {
    private String productId;
    private String productName;
    private double productPrice; // Unit price
    private int quantity;
    
    private String productSize;
    private String productSugar;
    private String productIce;
    private String productToppings;
    
    private String imageUrl; // URL (Firebase/Web) or Base64
    private int imageResId; // Local drawable resource ID
    
    private String category;

    public CartItem() {
        // Default constructor required for Firebase
    }

    // Constructor đầy đủ
    public CartItem(String productId, String productName, double productPrice, int quantity, 
                   String productSize, String productSugar, String productIce, String category,
                   String imageUrl) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.productSize = productSize;
        this.productSugar = productSugar;
        this.productIce = productIce;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    // Constructor legacy (để tương thích code cũ nếu có)
    public CartItem(String productId, String productName, double productPrice, String imageUrl,
                   int quantity, String productSize, String productSugar, String productIce,
                   String toppings, String category) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.productSize = productSize;
        this.productSugar = productSugar;
        this.productIce = productIce;
        this.productToppings = toppings;
        this.category = category;
    }

    // --- Parcelable Implementation ---
    protected CartItem(Parcel in) {
        productId = in.readString();
        productName = in.readString();
        productPrice = in.readDouble();
        quantity = in.readInt();
        productSize = in.readString();
        productSugar = in.readString();
        productIce = in.readString();
        productToppings = in.readString();
        imageUrl = in.readString();
        imageResId = in.readInt();
        category = in.readString();
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(productName);
        dest.writeDouble(productPrice);
        dest.writeInt(quantity);
        dest.writeString(productSize);
        dest.writeString(productSugar);
        dest.writeString(productIce);
        dest.writeString(productToppings);
        dest.writeString(imageUrl);
        dest.writeInt(imageResId);
        dest.writeString(category);
    }

    // --- GETTERS & SETTERS ---

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    // Alias getter
    public String getName() { return productName; }
    public void setName(String name) { this.productName = name; }

    public double getProductPrice() { return productPrice; }
    public void setProductPrice(double productPrice) { this.productPrice = productPrice; }
    
    // Alias getter
    public double getUnitPrice() { return productPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getProductSize() { return productSize; }
    public void setProductSize(String productSize) { this.productSize = productSize; }
    // Alias
    public String getSize() { return productSize; }
    public void setSize(String size) { this.productSize = size; }

    public String getProductSugar() { return productSugar; }
    public void setProductSugar(String productSugar) { this.productSugar = productSugar; }
    // Alias
    public String getSugar() { return productSugar; }
    public void setSugar(String sugar) { this.productSugar = sugar; }

    public String getProductIce() { return productIce; }
    public void setProductIce(String productIce) { this.productIce = productIce; }
    // Alias
    public String getIce() { return productIce; }
    public void setIce(String ice) { this.productIce = ice; }
    
    public String getProductToppings() { return productToppings; }
    public void setProductToppings(String productToppings) { this.productToppings = productToppings; }
    // Alias
    public String getToppings() { return productToppings; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    // Alias
    public String getProductImage() { return imageUrl; }
    public void setProductImage(String productImage) { this.imageUrl = productImage; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    
    public double getTotalPrice() {
        return productPrice * quantity;
    }
}