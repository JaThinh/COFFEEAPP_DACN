package com.example.myapplication.database;

import com.example.myapplication.model.Product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseHelper {

    private DatabaseReference databaseReference;

    public FirebaseDatabaseHelper() {
        // Lấy "root" của database và tạo một "nhánh" tên là "products"
        databaseReference = FirebaseDatabase.getInstance().getReference("products");
    }

    /**
     * Thêm hoặc cập nhật một sản phẩm trên Firebase.
     * Firebase sẽ dùng ID của sản phẩm làm khóa chính (key).
     * Nếu sản phẩm với ID này chưa có, nó sẽ được tạo mới.
     * Nếu đã có, nó sẽ được cập nhật.
     * @param product Sản phẩm cần thêm/cập nhật.
     */
    public void addOrUpdateProduct(Product product) {
        if (product != null && product.getId() != null) {
            // Dùng ID của product làm key trên Firebase
            databaseReference.child(product.getId()).setValue(product);
        }
    }
}
