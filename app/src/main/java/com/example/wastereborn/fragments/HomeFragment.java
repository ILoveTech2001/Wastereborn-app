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
import com.example.wastereborn.model.Notification;
import com.example.wastereborn.model.Reward;
import com.example.wastereborn.utils.SessionManager;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private TextView greetingText, userInitial, pointsText, levelText;
    private TextView recentActivityText, activityDate;
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
        pointsText = view.findViewById(R.id.points_text);
        levelText = view.findViewById(R.id.level_text);
        recentActivityText = view.findViewById(R.id.recent_activity_text);
        activityDate = view.findViewById(R.id.activity_date);
        cardOrderPickup = view.findViewById(R.id.card_order_pickup);
        cardVisitStore = view.findViewById(R.id.card_visit_store);

        // Set greeting timeout (hide after 20 seconds)
        new Handler().postDelayed(() -> greetingText.setVisibility(View.GONE), 20000);

        // Load real user data from session
        loadUserData();

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

    private void loadUserData() {
        if (sessionManager.isLoggedIn()) {
            // Set user initial
            userInitial.setText(sessionManager.getUserInitial());

            // Set greeting with user name
            String greeting = "Welcome back, " + sessionManager.getUserName().split(" ")[0] + "! 🌱";
            greetingText.setText(greeting);

            // Load points from API
            loadUserPoints();

            // Load recent activity
            loadRecentActivity();

            // Set default level (can be enhanced later)
            levelText.setText(sessionManager.isPremium() ? "Premium" : "Standard");
        } else {
            // Default values if not logged in
            userInitial.setText("U");
            pointsText.setText("0");
            levelText.setText("Guest");
            greetingText.setText("Welcome to WasteReborn! 🌱");
            recentActivityText.setText("Please log in to see your activity");
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
                        pointsText.setText(String.valueOf(points));
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Integer>> call, Throwable t) {
                // Handle error silently or show default
                pointsText.setText(String.valueOf(sessionManager.getPointsBalance()));
            }
        });
    }

    private void loadRecentActivity() {
        String token = "Bearer " + sessionManager.getToken();

        apiService.getRecentActivity(token).enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // For now, just show that there's activity
                    recentActivityText.setText("Recent pickup completed successfully");
                    activityDate.setText("Today");
                } else {
                    recentActivityText.setText("No recent activity");
                    activityDate.setText("");
                }
            }

            @Override
            public void onFailure(Call<List<Object>> call, Throwable t) {
                recentActivityText.setText("Unable to load recent activity");
                activityDate.setText("");
            }
        });
    }
}

