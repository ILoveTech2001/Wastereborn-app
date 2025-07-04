package com.example.wastereborn;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AuthActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager;
    AuthPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        pagerAdapter = new AuthPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) tab.setText("Login");
                    else tab.setText("Sign Up");
                }).attach();

        // Get selected tab from intent
        int selectedTab = getIntent().getIntExtra("selectedTab", 0);
        viewPager.setCurrentItem(selectedTab, false);
    }

}

