package com.example.myapplication.UI.Home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.R;
import com.example.myapplication.model.Banner;
import com.example.myapplication.model.Category;
import com.example.myapplication.model.Product;
import com.example.myapplication.model.Slider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<Slider>> _banners = new MutableLiveData<>();
    public final LiveData<List<Slider>> banners = _banners;

    private final MutableLiveData<List<Category>> _categories = new MutableLiveData<>();
    public final LiveData<List<Category>> categories = _categories;

    private final MutableLiveData<List<Product>> _allProducts = new MutableLiveData<>();
    public final LiveData<List<Product>> allProducts = _allProducts;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public final LiveData<Boolean> isLoading = _isLoading;

    private final DatabaseReference mDatabase;

    public HomeViewModel() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void loadData() {
        _isLoading.setValue(true);
        
        loadBanners();
        loadCategories();
        loadProducts();
    }

    private void loadBanners() {
        mDatabase.child("banners").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Slider> sliderList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Banner banner = data.getValue(Banner.class);
                    if (banner != null && banner.getImageUrl() != null) {
                        sliderList.add(new Slider(banner.getImageUrl()));
                    }
                }
                _banners.postValue(sliderList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void loadCategories() {
        mDatabase.child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> categoryList = new ArrayList<>();
                // Sửa lỗi: Dùng String tên ảnh thay vì Resource ID
                categoryList.add(new Category("all", "Tất cả", "ic_category_all")); 
                
                for (DataSnapshot data : snapshot.getChildren()) {
                    Category category = data.getValue(Category.class);
                    if (category != null) {
                        category.setId(data.getKey());
                        categoryList.add(category);
                    }
                }
                _categories.postValue(categoryList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void loadProducts() {
        mDatabase.child("products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> productList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Product product = data.getValue(Product.class);
                    if (product != null) {
                        if (product.getId() == null) {
                            product.setId(data.getKey());
                        }
                        productList.add(product);
                    }
                }
                _allProducts.postValue(productList);
                _isLoading.postValue(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                _isLoading.postValue(false);
            }
        });
    }
}