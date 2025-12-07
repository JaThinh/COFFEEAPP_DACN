package com.example.myapplication.UI.Home;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;

// Activity mới để thay thế HomeActivity
public class MainHubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}
