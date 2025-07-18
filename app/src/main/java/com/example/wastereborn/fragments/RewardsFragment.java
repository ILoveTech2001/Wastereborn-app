package com.example.wastereborn.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wastereborn.R;
import com.example.wastereborn.adapters.RewardAdapter;
import com.example.wastereborn.api.ApiClient;
import com.example.wastereborn.api.ApiService;
import com.example.wastereborn.model.Reward;
import com.example.wastereborn.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RewardsFragment extends Fragment {
    
    private TextView totalPointsText;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RewardAdapter adapter;
    private List<Reward> rewards;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);
        
        initViews(view);
        setupRecyclerView();
        loadRewards();
        loadTotalPoints();
        
        return view;
    }

    private void initViews(View view) {
        totalPointsText = view.findViewById(R.id.text_total_points);
        recyclerView = view.findViewById(R.id.recycler_rewards);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        
        sessionManager = new SessionManager(getContext());
        apiService = ApiClient.getClient().create(ApiService.class);
        
        rewards = new ArrayList<>();
        
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadRewards();
            loadTotalPoints();
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_green);
    }

    private void setupRecyclerView() {
        adapter = new RewardAdapter(rewards, new RewardAdapter.OnRewardClickListener() {
            @Override
            public void onRewardClick(Reward reward) {
                // Handle reward click - maybe show details
            }

            @Override
            public void onRedeemClick(Reward reward) {
                // Handle redeem action
                redeemReward(reward);
            }
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadTotalPoints() {
        String token = "Bearer " + sessionManager.getToken();
        
        apiService.getAvailablePoints(token).enqueue(new Callback<Map<String, Integer>>() {
            @Override
            public void onResponse(Call<Map<String, Integer>> call, Response<Map<String, Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Integer points = response.body().get("points");
                    if (points != null) {
                        totalPointsText.setText(String.valueOf(points));
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Integer>> call, Throwable t) {
                totalPointsText.setText(String.valueOf(sessionManager.getPointsBalance()));
            }
        });
    }

    private void loadRewards() {
        swipeRefreshLayout.setRefreshing(true);
        
        String token = "Bearer " + sessionManager.getToken();
        
        apiService.getUserRewards(token).enqueue(new Callback<List<Reward>>() {
            @Override
            public void onResponse(Call<List<Reward>> call, Response<List<Reward>> response) {
                swipeRefreshLayout.setRefreshing(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    rewards.clear();
                    rewards.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load rewards", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Reward>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redeemReward(Reward reward) {
        // This would typically open a redeem dialog or process
        Toast.makeText(getContext(), "Redeem functionality coming soon!", Toast.LENGTH_SHORT).show();
    }
}
