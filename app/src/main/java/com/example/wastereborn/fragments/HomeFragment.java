package com.example.wastereborn.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.wastereborn.R;
import com.example.wastereborn.api.ApiClient;
import com.example.wastereborn.api.ApiService;

import com.example.wastereborn.utils.SessionManager;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private TextView greetingText, userInitial, totalOrders, percentRecycled;
    private CardView cardOrderPickup, cardVisitStore;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize session manager
        sessionManager = new SessionManager(requireContext());
        apiService = ApiClient.getClient().create(ApiService.class);

        // Initialize views
        greetingText = view.findViewById(R.id.greeting_text);
        userInitial = view.findViewById(R.id.user_initial);
        percentRecycled = view.findViewById(R.id.percent_recycled);
        totalOrders = view.findViewById(R.id.total_orders);
        cardOrderPickup = view.findViewById(R.id.card_order_pickup);
        cardVisitStore = view.findViewById(R.id.card_visit_store);

        // Set greeting timeout (hide after 20 seconds)
        new Handler().postDelayed(() -> greetingText.setVisibility(View.GONE), 20000);

        // Load real user data from session
        loadUserData();

        // Load real orders count
        loadOrdersCount();

        // Set card click actions
        cardOrderPickup.setOnClickListener(v -> {
            // Navigate to PickupFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PickupFragment())
                    .addToBackStack(null)
                    .commit();
        });

        cardVisitStore.setOnClickListener(v -> {
            // Navigate to MarketPlaceFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MarketPlaceFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment becomes visible (e.g., after payment)
        System.out.println("🏠 HomeFragment: onResume() - Refreshing data");
        if (sessionManager.isLoggedIn()) {
            loadOrdersCount();
        }
    }

    private void loadUserData() {
        if (sessionManager.isLoggedIn()) {
            // Set user initial
            userInitial.setText(sessionManager.getUserInitial());

            // Set greeting with user name
            String greeting = "Welcome back, " + sessionManager.getUserName().split(" ")[0] + "! 🌱";
            greetingText.setText(greeting);

            // Load points and update percentage
            loadUserPoints();
        } else {
            // Default values if not logged in
            userInitial.setText("U");
            percentRecycled.setText("0%");
            totalOrders.setText("0");
            greetingText.setText("Welcome to WasteReborn! 🌱");
        }
    }

    private void loadUserPoints() {
        String token = "Bearer " + sessionManager.getToken();

        apiService.getAvailablePoints(token).enqueue(new Callback<Map<String, Integer>>() {
            @Override
            public void onResponse(Call<Map<String, Integer>> call, Response<Map<String, Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Integer points = response.body().get("points");
                    if (points != null) {
                        // Update percentage based on points (example calculation)
                        int percentage = Math.min(100, points / 10);
                        percentRecycled.setText(percentage + "%");
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Integer>> call, Throwable t) {
                // Handle error silently or show default
                int points = sessionManager.getPointsBalance();
                int percentage = Math.min(100, points / 10);
                percentRecycled.setText(percentage + "%");
            }
        });
    }



    private void loadOrdersCount() {
        if (!sessionManager.isLoggedIn()) {
            totalOrders.setText("0");
            return;
        }

        String token = sessionManager.getAuthHeader();

        apiService.getUserOrders(token).enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int ordersCount = response.body().size();
                    totalOrders.setText(String.valueOf(ordersCount));
                    System.out.println("🏠 HomeFragment: Updated orders count to " + ordersCount);
                } else {
                    totalOrders.setText("0");
                    System.err.println("🏠 HomeFragment: Failed to load orders count");
                }
            }

            @Override
            public void onFailure(Call<List<Object>> call, Throwable t) {
                totalOrders.setText("0");
                System.err.println("🏠 HomeFragment: Error loading orders count: " + t.getMessage());
            }
        });
    }
}

