package com.example.myapplication.UI.Home;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;

public class AllProductsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products_list);

        // Load the ProductCategoryFragment with "all" categoryId
        if (savedInstanceState == null) {
            ProductCategoryFragment fragment = ProductCategoryFragment.newInstance("all");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
