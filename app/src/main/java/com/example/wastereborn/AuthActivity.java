package com.example.wastereborn;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.wastereborn.fragments.LoginFragment;
import com.example.wastereborn.fragments.SignupFragment;

public class AuthActivity extends AppCompatActivity {

    private Button btnSignIn, btnSignUp;
    private LinearLayout welcomeSection;
    private ViewPager2 viewPager;
    private AuthPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Initialize views
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignUp = findViewById(R.id.btn_sign_up);
        welcomeSection = findViewById(R.id.welcome_section);
        viewPager = findViewById(R.id.view_pager);

        // Set up ViewPager (hidden by default)
        pagerAdapter = new AuthPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Set up button click listeners
        btnSignIn.setOnClickListener(v -> showLoginFragment());
        btnSignUp.setOnClickListener(v -> showSignupFragment());

        // Check if we should show a specific fragment from intent
        int selectedTab = getIntent().getIntExtra("selectedTab", -1);
        if (selectedTab == 0) {
            showLoginFragment();
        } else if (selectedTab == 1) {
            showSignupFragment();
        }
    }

    private void showLoginFragment() {
        // Hide welcome section and show login
        welcomeSection.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        viewPager.setCurrentItem(0, true);
    }

    private void showSignupFragment() {
        // Hide welcome section and show signup
        welcomeSection.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        viewPager.setCurrentItem(1, true);
    }

    @Override
    public void onBackPressed() {
        // If ViewPager is visible, go back to welcome screen
        if (viewPager.getVisibility() == View.VISIBLE) {
            welcomeSection.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}

