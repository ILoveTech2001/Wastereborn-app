package com.example.wastereborn;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private Button btnLogin, btnSignup;
    private ImageView recyclingLogo;
    private LinearLayout welcomeSection;
    private Handler animationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Initialize views
        initializeViews();

        // Set up click listeners
        setupClickListeners();

        // Start welcome animations
        startWelcomeAnimations();
    }

    private void initializeViews() {
        btnLogin = findViewById(R.id.btn_login);
        btnSignup = findViewById(R.id.btn_signup);
        recyclingLogo = findViewById(R.id.recycling_logo);
        welcomeSection = findViewById(R.id.welcome_section);
        animationHandler = new Handler();
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> {
            Intent i = new Intent(WelcomeActivity.this, AuthActivity.class);
            i.putExtra("selectedTab", 0); // Show login tab
            startActivity(i);
        });

        btnSignup.setOnClickListener(v -> {
            Intent i = new Intent(WelcomeActivity.this, AuthActivity.class);
            i.putExtra("selectedTab", 1); // Show signup tab
            startActivity(i);
        });
    }

    private void startWelcomeAnimations() {
        // Hide elements initially
        recyclingLogo.setAlpha(0f);
        btnLogin.setAlpha(0f);
        btnSignup.setAlpha(0f);

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
            btnLogin.startAnimation(buttonAnimation);
            btnLogin.setAlpha(1f);
        }, 800);

        animationHandler.postDelayed(() -> {
            Animation buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up_fade_in);
            btnSignup.startAnimation(buttonAnimation);
            btnSignup.setAlpha(1f);
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (animationHandler != null) {
            animationHandler.removeCallbacksAndMessages(null);
        }
    }
}

