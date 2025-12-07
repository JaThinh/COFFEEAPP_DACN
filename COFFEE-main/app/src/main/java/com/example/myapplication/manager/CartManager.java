package com.example.myapplication.manager;

import com.example.myapplication.Item.CartItem;
import com.example.myapplication.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartManager {

    private static CartManager instance;
    private Map<String, CartItem> cartItems;
    private double totalPriceBeforeClear;
    private DatabaseReference cartReference;

    private CartManager() {
        cartItems = new HashMap<>();
        // Khởi tạo Firebase reference cho giỏ hàng

        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "anonymous";
        cartReference = FirebaseDatabase.getInstance().getReference("carts").child(userId);
    }

    public static synchronized CartManager getInstance()
    {
        if (instance == null)
        {
            instance = new CartManager();
        }
        return instance;
    }

    public void addProduct(Product product) {
        if (cartItems.containsKey(product.getName()))
        {
            CartItem item = cartItems.get(product.getName());
            item.setQuantity(item.getQuantity() + 1);
        } else {
            cartItems.put(product.getName(), new CartItem(product, 1));
        }
        // Lưu giỏ hàng lên Firebase
        updateCartOnFirebase();
    }

    public void removeProduct(Product product) {
        if (cartItems.containsKey(product.getName()))
        {
            CartItem item = cartItems.get(product.getName());
            if (item.getQuantity() > 1)
            {
                item.setQuantity(item.getQuantity() - 1);
            } else {
                cartItems.remove(product.getName());
            }
            // Lưu giỏ hàng lên Firebase
            updateCartOnFirebase();
        }
    }

    public void removeAllProduct(Product product) {
        if (cartItems.containsKey(product.getName())) {
            cartItems.remove(product.getName());
            // Lưu giỏ hàng lên Firebase
            updateCartOnFirebase();
        }
    }

    public int getProductQuantity(Product product) {
        if (cartItems.containsKey(product.getName())) {
            return cartItems.get(product.getName()).getQuantity();
        }
        return 0;
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems.values());
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems.values()) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public double getTotalPriceBeforeClear() {
        return totalPriceBeforeClear;
    }

    public void clearCart() {
        totalPriceBeforeClear = getTotalPrice();
        cartItems.clear();
        // Xóa giỏ hàng trên Firebase
        cartReference.removeValue();
    }

    private void updateCartOnFirebase() {
        // Lưu toàn bộ giỏ hàng lên Firebase
        cartReference.setValue(cartItems);
    }
}