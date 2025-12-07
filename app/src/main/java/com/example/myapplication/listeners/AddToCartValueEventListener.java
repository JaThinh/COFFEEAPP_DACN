package com.example.myapplication.listeners;

import android.widget.Toast;
import androidx.annotation.NonNull;

import com.example.myapplication.UI.Home.HomeActivity;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class AddToCartValueEventListener implements ValueEventListener {

    private final HomeActivity activity;
    private final DatabaseReference cartItemRef;
    private final Product product;

    public AddToCartValueEventListener(HomeActivity activity, DatabaseReference cartItemRef, Product product) {
        this.activity = activity;
        this.cartItemRef = cartItemRef;
        this.product = product;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (snapshot.exists() && snapshot.child("quantity").exists()) {
            Integer currentQuantity = snapshot.child("quantity").getValue(Integer.class);
            if (currentQuantity == null) currentQuantity = 0;
            cartItemRef.child("quantity").setValue(currentQuantity + 1)
                    .addOnSuccessListener(aVoid -> activity.showAddToCartSnackBar(product.getName()));
        } else {
            CartItem newItem = new CartItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getImageUrl(),
                    1,      // quantity
                    null,   // size
                    null,   // sugar
                    null,   // ice
                    null,   // toppings
                    product.getCategoryName() // category
            );
            cartItemRef.setValue(newItem)
                    .addOnSuccessListener(aVoid -> activity.showAddToCartSnackBar(product.getName()));
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        Toast.makeText(activity, "Thêm vào giỏ hàng thất bại: " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
