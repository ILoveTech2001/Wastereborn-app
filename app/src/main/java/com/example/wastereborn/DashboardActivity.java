package com.example.wastereborn;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.wastereborn.fragments.ChatbotFragment;
import com.example.wastereborn.fragments.HistoryFragment;
import com.example.wastereborn.fragments.HomeFragment;
import com.example.wastereborn.fragments.MarketPlaceFragment;
import com.example.wastereborn.fragments.NotificationsFragment;
import com.example.wastereborn.fragments.OrdersFragment;
import com.example.wastereborn.fragments.PickupFragment;
import com.example.wastereborn.fragments.RewardFragment;
import com.google.android.material.navigation.NavigationView;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer Layout and Navigation View
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Toggle for hamburger icon
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Load HomeFragment by default
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId(); // Get the ID once

        if (itemId == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment()).commit();
        } else if (itemId == R.id.nav_pickup) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PickupFragment()).commit();
        } else if (itemId == R.id.nav_marketplace) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MarketPlaceFragment()).commit();
        } else if (itemId == R.id.nav_orders) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new OrdersFragment()).commit();
        } else if (itemId == R.id.nav_rewards) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RewardFragment()).commit();
        } else if (itemId == R.id.nav_history) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HistoryFragment()).commit();
        } else if (itemId == R.id.nav_notifications) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new NotificationsFragment()).commit();
        } else if (itemId == R.id.nav_chatbot) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ChatbotFragment()).commit();
        } else if (itemId == R.id.nav_logout) {
            // TODO: Implement logout logic (e.g., clear SharedPreferences, navigate to Login)
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
