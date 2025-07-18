package com.example.wastereborn;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
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
    private ImageView recyclingLogo;
    private ViewPager2 viewPager;
    private AuthPagerAdapter pagerAdapter;
    private Handler animationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Initialize views
        initializeViews();

        // Set up ViewPager (hidden by default)
        setupViewPager();

        // Set up button click listeners
        setupClickListeners();

        // Start welcome animations
        startWelcomeAnimations();

        // Check if we should show a specific fragment from intent
        int selectedTab = getIntent().getIntExtra("selectedTab", -1);
        if (selectedTab == 0) {
            showLoginFragment();
        } else if (selectedTab == 1) {
            showSignupFragment();
        }
    }

    private void initializeViews() {
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignUp = findViewById(R.id.btn_sign_up);
        welcomeSection = findViewById(R.id.welcome_section);
        recyclingLogo = findViewById(R.id.recycling_logo);
        viewPager = findViewById(R.id.view_pager);
        animationHandler = new Handler();
    }

    private void setupViewPager() {
        pagerAdapter = new AuthPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
    }

    private void setupClickListeners() {
        btnSignIn.setOnClickListener(v -> showLoginFragment());
        btnSignUp.setOnClickListener(v -> showSignupFragment());
    }

    private void startWelcomeAnimations() {
        // Hide elements initially
        recyclingLogo.setAlpha(0f);
        btnSignIn.setAlpha(0f);
        btnSignUp.setAlpha(0f);

        // Animate recycling logo
        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_fade_in);
        recyclingLogo.startAnimation(logoAnimation);
        recyclingLogo.setAlpha(1f);

        // Animate welcome section
        Animation welcomeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_scale);
        welcomeSection.startAnimation(welcomeAnimation);

        // Animate buttons with delay
        animationHandler.postDelayed(() -> {
            Animation buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up_fade_in);
            btnSignIn.startAnimation(buttonAnimation);
            btnSignIn.setAlpha(1f);
        }, 800);

        animationHandler.postDelayed(() -> {
            Animation buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up_fade_in);
            btnSignUp.startAnimation(buttonAnimation);
            btnSignUp.setAlpha(1f);
        }, 1000);
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
        // If ViewPager is visible, go back to welcome screen with animation
        if (viewPager.getVisibility() == View.VISIBLE) {
            showWelcomeScreen();
        } else {
            super.onBackPressed();
        }
    }

    private void showWelcomeScreen() {
        // Animate back to welcome screen
        Animation fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        viewPager.startAnimation(fadeOut);

        animationHandler.postDelayed(() -> {
            welcomeSection.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);

            // Re-animate welcome elements
            Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            welcomeSection.startAnimation(fadeIn);
        }, 200);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (animationHandler != null) {
            animationHandler.removeCallbacksAndMessages(null);
        }
    }
}

