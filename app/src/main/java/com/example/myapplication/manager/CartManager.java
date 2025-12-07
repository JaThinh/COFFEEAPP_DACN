package com.example.myapplication.manager;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myapplication.model.CartItem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CartManager {
    private static final String TAG = "CartManager";
    private static CartManager instance;
    
    // Danh sách giỏ hàng local
    private final List<CartItem> cartItemList;
    
    // LiveData để UI lắng nghe
    private final MutableLiveData<List<CartItem>> cartItemsLiveData;
    private final MutableLiveData<Boolean> isLoadingLiveData;
    
    private String currentUserId;

    private CartManager() {
        cartItemList = new ArrayList<>();
        cartItemsLiveData = new MutableLiveData<>();
        cartItemsLiveData.setValue(cartItemList);
        
        isLoadingLiveData = new MutableLiveData<>();
        isLoadingLiveData.setValue(false);
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public LiveData<List<CartItem>> getCartItemsLiveData() {
        return cartItemsLiveData;
    }
    
    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    // Thêm mới hoặc cập nhật số lượng
    public void addToCart(CartItem newItem) {
        if (newItem == null) return;
        
        // Giả lập loading (nếu cần)
        isLoadingLiveData.setValue(true);

        boolean exists = false;
        for (CartItem item : cartItemList) {
            // Logic kiểm tra trùng: Cùng ProductID + Size + Sugar + Ice
            if (isSameProduct(item, newItem)) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                exists = true;
                break;
            }
        }

        if (!exists) {
            cartItemList.add(newItem);
        }

        updateLiveData();
        isLoadingLiveData.setValue(false);
        Log.d(TAG, "Added/Updated item: " + newItem.getProductName());
    }
    
    // Tăng số lượng item có sẵn
    public void addCartItem(CartItem item, int quantity) {
        for (CartItem existingItem : cartItemList) {
            if (isSameProduct(existingItem, item)) {
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                updateLiveData();
                return;
            }
        }
        // Nếu không tìm thấy (hiếm khi xảy ra nếu gọi từ giỏ hàng), thêm mới
        item.setQuantity(quantity);
        addToCart(item);
    }

    // Giảm số lượng hoặc xóa item
    public void removeCartItem(CartItem itemToRemove) {
        Iterator<CartItem> iterator = cartItemList.iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (isSameProduct(item, itemToRemove)) {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                } else {
                    iterator.remove();
                }
                updateLiveData();
                return;
            }
        }
    }
    
    // Xóa hẳn item khỏi giỏ
    public void removeAllOfItem(CartItem itemToRemove) {
        Iterator<CartItem> iterator = cartItemList.iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (isSameProduct(item, itemToRemove)) {
                iterator.remove();
                updateLiveData();
                return;
            }
        }
    }
    
    public void clearCart() {
        cartItemList.clear();
        updateLiveData();
    }
    
    private void updateLiveData() {
        cartItemsLiveData.setValue(new ArrayList<>(cartItemList));
    }
    
    private boolean isSameProduct(CartItem item1, CartItem item2) {
        if (!item1.getProductId().equals(item2.getProductId())) return false;
        
        // So sánh Size
        String s1 = item1.getSize() != null ? item1.getSize() : "";
        String s2 = item2.getSize() != null ? item2.getSize() : "";
        if (!s1.equals(s2)) return false;
        
        // So sánh Sugar
        String u1 = item1.getSugar() != null ? item1.getSugar() : "";
        String u2 = item2.getSugar() != null ? item2.getSugar() : "";
        if (!u1.equals(u2)) return false;
        
        // So sánh Ice
        String i1 = item1.getIce() != null ? item1.getIce() : "";
        String i2 = item2.getIce() != null ? item2.getIce() : "";
        return i1.equals(i2);
    }
    
    public void updateUserId(String userId) {
        this.currentUserId = userId;
        if (userId == null) {
            clearCart();
        }
    }
    
    public void clearCartOnLogout() {
        clearCart();
        this.currentUserId = null;
    }
}