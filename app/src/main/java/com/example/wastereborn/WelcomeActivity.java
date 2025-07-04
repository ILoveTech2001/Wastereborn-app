package com.example.wastereborn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    Button btnLogin, btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btnLogin = findViewById(R.id.btn_login);
        btnSignup = findViewById(R.id.btn_signup);

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
}

