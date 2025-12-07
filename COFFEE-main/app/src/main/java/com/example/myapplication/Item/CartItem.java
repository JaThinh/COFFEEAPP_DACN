package com.example.myapplication.Item;

import com.example.myapplication.model.Product; // Đảm bảo import Product đúng package
import java.io.Serializable;
import java.util.List; // Import List

// Đại diện cho một mục trong giỏ hàng, bao gồm các tùy chọn đã chọn
public class CartItem implements Serializable {
    private Product product;
    private int quantity;
    private double totalPrice; // Tổng giá của mục này (bao gồm size, topping)
    private String selectedSize;
    private List<String> selectedToppings; // Lưu danh sách tên topping đã chọn

    public CartItem()
    {
        // Constructor mặc định cho Firebase
    }

    // Cập nhật constructor  bao gồm size và toppings đã chọn
    public CartItem(Product product, String selectedSize, List<String> selectedToppings, int quantity, double totalPrice)
    {
        this.product = product;
        this.selectedSize = selectedSize;
        this.selectedToppings = selectedToppings;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    // Constructor
    public CartItem(Product product, int quantity)
    {
        this(product, "", null, quantity, product != null ? product.getPrice() * quantity : 0.0);
    }

    public Product getProduct()
    {
        return product;
    }

    public void setProduct(Product product)
    {
        this.product = product;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public double getTotalPrice()
    {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice)
    {
        this.totalPrice = totalPrice;
    }

    // Getter và Setter cho selectedSize
    public String getSelectedSize()
    {
        return selectedSize;
    }

    public void setSelectedSize(String selectedSize)
    {
        this.selectedSize = selectedSize;
    }

    // Getter và Setter cho selectedToppings
    public List<String> getSelectedToppings()
    {
        return selectedToppings;
    }

    public void setSelectedToppings(List<String> selectedToppings)
    {
        this.selectedToppings = selectedToppings;
    }
}
