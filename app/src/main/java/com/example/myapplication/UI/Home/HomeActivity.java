package com.example.myapplication.UI.Home;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.example.myapplication.R;
import com.example.myapplication.UI.Cart.CartActivity;
import com.example.myapplication.UI.Detail.ProductDetailActivity;
import com.example.myapplication.UI.support.ChatbotActivity;
import com.example.myapplication.UI.profile.ProfileActivity;
import com.example.myapplication.UI.settings.SettingsActivity;
import com.example.myapplication.UI.support.SupportActivity;
import com.example.myapplication.adapter.CategoryAdapter;
import com.example.myapplication.adapter.ProductGridAdapter;
import com.example.myapplication.adapter.SearchSuggestionAdapter;
import com.example.myapplication.adapter.SliderAdapter;
import com.example.myapplication.manager.CartManager;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.Category;
import com.example.myapplication.model.Product;
import com.example.myapplication.model.ProductGrid;
import com.example.myapplication.model.Slider;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.relex.circleindicator.CircleIndicator3;

public class HomeActivity extends AppCompatActivity implements ProductGridAdapter.OnProductGridClickListener, SliderAdapter.OnSliderClickListener, SearchSuggestionAdapter.OnSuggestionClickListener, CategoryAdapter.OnCategoryClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ViewPager2 bannerViewPager;
    private RecyclerView productRecyclerView;
    private RecyclerView categoryRecyclerView;
    private RecyclerView searchSuggestionsRecyclerView;
    private CircleIndicator3 indicator;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private SearchView searchView;
    private ShimmerFrameLayout shimmerViewContainer;
    private NestedScrollView mainContentScroll;
    private TextView tvWelcomeMessage;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabCart;
    
    private ImageView ivCart;
    private ImageView ivNotification;
    private TextView tvCartCount;

    private SliderAdapter sliderAdapter;
    private ProductGridAdapter productGridAdapter;
    private SearchSuggestionAdapter searchSuggestionAdapter;
    private CategoryAdapter categoryAdapter;

    private List<Slider> sliderList;
    private List<ProductGrid> allProductsList;
    private List<String> suggestionList;
    private List<Category> categoryList;

    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (sliderList != null && !sliderList.isEmpty() && bannerViewPager.getCurrentItem() == sliderList.size() - 1) {
                bannerViewPager.setCurrentItem(0);
            } else {
                bannerViewPager.setCurrentItem(bannerViewPager.getCurrentItem() + 1);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        initEvents(); 
        setupSwipeToRefresh();
        setupBottomNavigation();
        setupSearch();
        loadAndDisplayData();
        
        CartManager.getInstance().getCartItemsLiveData().observe(this, cartItems -> {
            int cartCount = cartItems != null ? cartItems.size() : 0;
            updateCartCount(cartCount);
        });
    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        bannerViewPager = findViewById(R.id.bannerViewPager);
        indicator = findViewById(R.id.banner_indicator);
        tvWelcomeMessage = findViewById(R.id.tv_welcome_message);
        productRecyclerView = findViewById(R.id.recycler_view_products);
        categoryRecyclerView = findViewById(R.id.recycler_view_categories);
        searchView = findViewById(R.id.search_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        fabCart = findViewById(R.id.fab_cart);
        searchSuggestionsRecyclerView = findViewById(R.id.recycler_view_search_suggestions);
        shimmerViewContainer = findViewById(R.id.shimmer_view_container);
        mainContentScroll = findViewById(R.id.main_content_scroll);
        
        ivCart = findViewById(R.id.iv_cart);
        ivNotification = findViewById(R.id.iv_notification);
        tvCartCount = findViewById(R.id.tv_cart_count);

        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productRecyclerView.setHasFixedSize(true);
        productRecyclerView.setNestedScrollingEnabled(false); 

        allProductsList = new ArrayList<>();
        productGridAdapter = new ProductGridAdapter(new ArrayList<>(), this);
        productRecyclerView.setAdapter(productGridAdapter);
        
        fabCart.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, CartActivity.class)));
        
        FloatingActionButton fabChat = findViewById(R.id.fab_chat);
        fabChat.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ChatbotActivity.class)));

        FloatingActionButton fabAI = findViewById(R.id.fab_ai_recommend);
        fabAI.setOnClickListener(v -> getAIRecommendation());
        
        fabAI.postDelayed(() -> {
            ObjectAnimator rotate = ObjectAnimator.ofFloat(fabAI, "rotation", 0f, 15f, -15f, 10f, -10f, 0f);
            rotate.setDuration(1200);
            rotate.setRepeatMode(android.animation.ValueAnimator.RESTART);
            rotate.setRepeatCount(1);
            rotate.start();
        }, 2000);
    }

    private void initEvents() {
        if (ivCart != null) {
            ivCart.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, CartActivity.class)));
        }

        if (ivNotification != null) {
            ivNotification.setOnClickListener(v -> Toast.makeText(HomeActivity.this, "Bạn chưa có thông báo mới nào", Toast.LENGTH_SHORT).show());
        }
    }

    private void loadAndDisplayData() {
        shimmerViewContainer.startShimmer();
        shimmerViewContainer.setVisibility(View.VISIBLE);
        mainContentScroll.setVisibility(View.GONE);

        setupSlider();
        setupCategoryRecycler(); 
        loadMockProducts(); 
        
        uploadDataToFirebase();
        
        swipeRefreshLayout.setRefreshing(false);
    }

    private void setupSlider() {
        sliderList = new ArrayList<>();
        sliderList.add(new Slider(R.drawable.banner_khuyenmai1, "Khuyến mãi 1"));
        sliderList.add(new Slider(R.drawable.banner_khuyenmai2, "Khuyến mãi 2"));
        sliderList.add(new Slider(R.drawable.banner_khuyenmai3, "Khuyến mãi 3"));
        sliderList.add(new Slider(R.drawable.banner_khuyenmai5, "Khuyến mãi 5"));

        sliderAdapter = new SliderAdapter(this, sliderList, this);
        bannerViewPager.setAdapter(sliderAdapter);
        indicator.setViewPager(bannerViewPager);
        
        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });
    }

    private void setupCategoryRecycler() {
        categoryList = new ArrayList<>();
        categoryList.add(new Category("0", "Tất cả", "ic_category_all"));
        categoryList.add(new Category("1", "Cà phê", "ic_category_coffee"));
        categoryList.add(new Category("2", "Trà", "ic_category_tea"));
        categoryList.add(new Category("3", "Sinh tố", "ic_category_smoothie"));
        categoryList.add(new Category("4", "Bánh ngọt", "ic_category_pastry"));

        categoryAdapter = new CategoryAdapter(this, categoryList, this);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void filterProductsByCategory(String categoryName) {
        List<ProductGrid> filteredList;
        if (categoryName == null || categoryName.equals("Tất cả")) {
            filteredList = new ArrayList<>(allProductsList);
        } else {
            filteredList = allProductsList.stream()
                    .filter(p -> categoryName.equals(p.getCategory()))
                    .collect(Collectors.toList());
        }
        productGridAdapter.setProducts(filteredList);
    }

    private void loadMockProducts() {
        allProductsList.clear();
        // Đảm bảo giá tiền là kiểu số (double/int), KHÔNG phải String
        allProductsList.add(new ProductGrid("Cà phê Latte", "Hương vị Ý", 35000, R.drawable.ic_coffee, "Cà phê"));
        allProductsList.add(new ProductGrid("Cà phê Đen", "Đậm đà tỉnh táo", 25000, R.drawable.ic_coffee_1, "Cà phê"));
        allProductsList.add(new ProductGrid("Bạc Xỉu", "Ngọt ngào sữa đặc", 32000, R.drawable.ic_coffee_2, "Cà phê"));
        allProductsList.add(new ProductGrid("Cà phê Sữa Đá", "Chuẩn vị Việt", 29000, R.drawable.ic_coffee_3, "Cà phê"));
        allProductsList.add(new ProductGrid("Trà Sữa Trân Châu", "Dai ngon sần sật", 45000, R.drawable.ic_milktea, "Trà"));
        allProductsList.add(new ProductGrid("Trà Thảo Mộc", "Thanh lọc cơ thể", 30000, R.drawable.ic_tea_image, "Trà"));
        allProductsList.add(new ProductGrid("Trà Vải Nhiệt Đới", "Giải nhiệt mùa hè", 39000, R.drawable.ic_tea_image_1, "Trà"));
        allProductsList.add(new ProductGrid("Sinh tố Dâu Tây", "Vitamin C tự nhiên", 40000, R.drawable.ic_strawberry, "Sinh tố"));
        allProductsList.add(new ProductGrid("Sinh tố Bơ", "Béo ngậy bổ dưỡng", 42000, R.drawable.ic_placeholder5, "Sinh tố"));
        allProductsList.add(new ProductGrid("Nước ép Cam", "Tăng sức đề kháng", 35000, R.drawable.ic_juice, "Sinh tố"));
        allProductsList.add(new ProductGrid("Bánh Kem Dâu", "Ngọt ngào tình yêu", 45000, R.drawable.ic_cake, "Bánh ngọt"));
        allProductsList.add(new ProductGrid("Bánh Tiramisu", "Vị cafe cacao", 48000, R.drawable.ic_cake_1, "Bánh ngọt"));
        allProductsList.add(new ProductGrid("Bánh Croissant", "Vỏ giòn tan", 28000, R.drawable.ic_cake_2, "Bánh ngọt"));

        productGridAdapter.setProducts(allProductsList);
        
        shimmerViewContainer.stopShimmer();
        shimmerViewContainer.setVisibility(View.GONE);
        mainContentScroll.setVisibility(View.VISIBLE);
    }
    
    private void uploadDataToFirebase() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    for (ProductGrid item : allProductsList) {
                        String key = productsRef.push().getKey();
                        if (key != null) {
                            double price = parsePrice(String.valueOf(item.getPrice()));
                            Product product = new Product(item.getName(), item.getDescription(), price, item.getImageResId(), item.getCategory());
                            productsRef.child(key).setValue(product);
                        }
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setupSwipeToRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadAndDisplayData);
        swipeRefreshLayout.setColorSchemeResources(R.color.brown_500, R.color.brown_700, R.color.orange_500);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) return true;
            if (itemId == R.id.nav_menu) {
                mainContentScroll.smoothScrollTo(0, productRecyclerView.getTop());
                return true;
            }
            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            if (itemId == R.id.nav_support) {
                startActivity(new Intent(this, SupportActivity.class));
                return true;
            }
            return false;
        });
    }

    private void setupSearch() {
        searchSuggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        suggestionList = new ArrayList<>();
        searchSuggestionAdapter = new SearchSuggestionAdapter(suggestionList, this);
        searchSuggestionsRecyclerView.setAdapter(searchSuggestionAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    searchSuggestionsRecyclerView.setVisibility(View.VISIBLE);
                    filterSuggestions(newText);
                } else {
                    searchSuggestionsRecyclerView.setVisibility(View.GONE);
                }
                return true;
            }
        });
    }

    private void filterSuggestions(String query) {
        List<String> filtered = allProductsList.stream()
                .map(ProductGrid::getName)
                .filter(name -> name.toLowerCase().contains(query.toLowerCase()))
                .distinct()
                .limit(5)
                .collect(Collectors.toList());
        searchSuggestionAdapter.updateSuggestions(filtered);
    }
    
    private void performSearch(String query) {
        searchSuggestionsRecyclerView.setVisibility(View.GONE);
        List<ProductGrid> result = allProductsList.stream()
                .filter(p -> p.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        productGridAdapter.setProducts(result);
        mainContentScroll.smoothScrollTo(0, productRecyclerView.getTop());
    }

    // --- Implementation of Interface Methods ---

    @Override
    public void onCategoryClick(Category category) {
        filterProductsByCategory(category.getName());
    }

    @Override
    public void onSuggestionClick(String suggestion) {
        searchView.setQuery(suggestion, true);
    }

    @Override
    public void onProductClick(ProductGrid product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product);
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(ProductGrid product, int position) {
        Toast.makeText(this, "Đã thêm " + product.getName() + " vào yêu thích!", Toast.LENGTH_SHORT).show();
    }

    public void onAddToCartClick(ProductGrid product) {
        try {
            double price = parsePrice(String.valueOf(product.getPrice()));

            CartItem item = new CartItem(
                "id_temp_" + System.currentTimeMillis(),
                product.getName(),
                price,
                1, // quantity
                null, // size
                null, // sugar
                null, // ice
                product.getCategory(),
                product.getImageUrl()
            );
            item.setImageResId(product.getImageResId());

            CartManager.getInstance().addToCart(item);
            showAddToCartSnackBar(product.getName());

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Lỗi giá sản phẩm không hợp lệ.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAddToCartClick(ProductGrid product, View view) {
        onAddToCartClick(product); // Gọi lại hàm đã có
    }

    @Override
    public void onSliderClick(Slider slider) {
        Toast.makeText(this, slider.getTitle(), Toast.LENGTH_SHORT).show();
    }
    
    // --- Helper Methods ---

    private double parsePrice(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) {
            return 0.0;
        }
        try {
            String numericString = priceStr.replaceAll("[^\\d.]", "");
            if (numericString.isEmpty()) {
                return 0.0;
            }
            return Double.parseDouble(numericString);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public void showAddToCartSnackBar(String productName) {
        View view = findViewById(android.R.id.content);
        if (view != null) {
            Snackbar.make(view, "Đã thêm " + productName + " vào giỏ hàng", Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Đã thêm " + productName + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCartCount(int count) {
        if (tvCartCount != null) {
            if (count > 0) {
                tvCartCount.setText(String.valueOf(count));
                tvCartCount.setVisibility(View.VISIBLE);
            } else {
                tvCartCount.setVisibility(View.GONE);
            }
        }
    }

    private void getAIRecommendation() {
        Toast.makeText(this, "AI đang phân tích sở thích của bạn...", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
}