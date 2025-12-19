package com.example.myapplication.UI.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdminMaintainActivity extends AppCompatActivity {

    private Button applyChangesBtn, deleteBtn;
    private EditText name, price, description, category;
    private ImageView imageView;
    private String productID = "";
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain);

        // Ánh xạ View từ layout activity_admin_maintain.xml
        applyChangesBtn = findViewById(R.id.maintain_btn_apply_changes);
        name = findViewById(R.id.maintain_product_name);
        price = findViewById(R.id.maintain_product_price);
        description = findViewById(R.id.maintain_product_description);
        category = findViewById(R.id.maintain_product_category);
        imageView = findViewById(R.id.maintain_product_image);
        deleteBtn = findViewById(R.id.maintain_btn_delete);

        // Nhận ID sản phẩm từ Intent
        productID = getIntent().getStringExtra("pid");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

        // Hiển thị thông tin sản phẩm
        displaySpecificProductInfo();

        applyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyChanges();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProduct();
            }
        });
    }

    private void deleteProduct() {
        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(AdminMaintainActivity.this, AdminProductListActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(AdminMaintainActivity.this, "Sản phẩm đã được xóa thành công.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyChanges() {
        String pName = name.getText().toString();
        String pPrice = price.getText().toString();
        String pDescription = description.getText().toString();
        String pCategory = category.getText().toString();

        if (TextUtils.isEmpty(pName)) {
            Toast.makeText(this, "Vui lòng nhập tên sản phẩm...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pPrice)) {
            Toast.makeText(this, "Vui lòng nhập giá sản phẩm...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pDescription)) {
            Toast.makeText(this, "Vui lòng nhập mô tả sản phẩm...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pCategory)) {
            Toast.makeText(this, "Vui lòng nhập danh mục sản phẩm...", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productID);
            productMap.put("description", pDescription);
            productMap.put("price", Double.parseDouble(pPrice)); // Lưu ý parse về double cho đúng kiểu dữ liệu
            productMap.put("name", pName);
            productMap.put("categoryName", pCategory);

            productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminMaintainActivity.this, "Cập nhật thành công.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminMaintainActivity.this, AdminProductListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    private void displaySpecificProductInfo() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String pName = dataSnapshot.child("name").getValue().toString();
                    String pPrice = dataSnapshot.child("price").getValue().toString();
                    String pDescription = dataSnapshot.child("description").getValue().toString();
                    
                    // Kiểm tra null cho category vì có thể nó không tồn tại hoặc tên trường khác
                    String pCategory = "";
                    if (dataSnapshot.hasChild("categoryName")) {
                         pCategory = dataSnapshot.child("categoryName").getValue().toString();
                    } else if (dataSnapshot.hasChild("category")) {
                         pCategory = dataSnapshot.child("category").getValue().toString();
                    }

                    name.setText(pName);
                    price.setText(pPrice);
                    description.setText(pDescription);
                    category.setText(pCategory);
                    
                    if (dataSnapshot.hasChild("image")) {
                        String pImage = dataSnapshot.child("image").getValue().toString();
                        Glide.with(AdminMaintainActivity.this).load(pImage).into(imageView);
                    } else if (dataSnapshot.hasChild("imageUrl")) {
                         String pImage = dataSnapshot.child("imageUrl").getValue().toString();
                         Glide.with(AdminMaintainActivity.this).load(pImage).into(imageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
