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

public class HomeFragment extends Fragment {

    private TextView greetingText, userInitial, percentageRecycled, totalOrders;
    private CardView cardOrderPickup, cardVisitStore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        greetingText = view.findViewById(R.id.greeting_text);
        userInitial = view.findViewById(R.id.user_initial);
        percentageRecycled = view.findViewById(R.id.percent_recycled);
        totalOrders = view.findViewById(R.id.total_orders);
        cardOrderPickup = view.findViewById(R.id.card_order_pickup);
        cardVisitStore = view.findViewById(R.id.card_visit_store);

        // Set greeting timeout (hide after 20 seconds)
        new Handler().postDelayed(() -> greetingText.setVisibility(View.GONE), 20000);

        // Simulate user name
        String userName = "Alex"; // Replace this with real name from login/session
        userInitial.setText(userName.substring(0, 1).toUpperCase());

        // Simulate stats (replace with real data from backend later)
        percentageRecycled.setText("68% Recycled");
        totalOrders.setText("12 Orders");

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
}

