package com.example.wastereborn.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wastereborn.R;
import com.example.wastereborn.adapters.NotificationAdapter;
import com.example.wastereborn.api.ApiClient;
import com.example.wastereborn.api.ApiService;
import com.example.wastereborn.model.Notification;
import com.example.wastereborn.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NotificationAdapter adapter;
    private List<Notification> notifications;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        
        initViews(view);
        setupRecyclerView();
        loadNotifications();
        
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_notifications);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        
        sessionManager = new SessionManager(getContext());
        apiService = ApiClient.getClient().create(ApiService.class);
        
        notifications = new ArrayList<>();
        
        swipeRefreshLayout.setOnRefreshListener(this::loadNotifications);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_green);
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(notifications, new NotificationAdapter.OnNotificationClickListener() {
            @Override
            public void onNotificationClick(Notification notification) {
                markAsRead(notification);
            }

            @Override
            public void onNotificationLongClick(Notification notification) {
                // Handle long click - maybe show options
            }
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadNotifications() {
        swipeRefreshLayout.setRefreshing(true);
        
        String token = "Bearer " + sessionManager.getToken();
        
        apiService.getUserNotifications(token).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                swipeRefreshLayout.setRefreshing(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    notifications.clear();
                    notifications.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load notifications", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markAsRead(Notification notification) {
        if (notification.isUnread()) {
            String token = "Bearer " + sessionManager.getToken();
            
            apiService.markNotificationAsRead(token, notification.getId()).enqueue(new Callback<Notification>() {
                @Override
                public void onResponse(Call<Notification> call, Response<Notification> response) {
                    if (response.isSuccessful()) {
                        notification.setIsRead(true);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<Notification> call, Throwable t) {
                    // Handle error silently
                }
            });
        }
    }

    public void markAllAsRead() {
        String token = "Bearer " + sessionManager.getToken();
        
        apiService.markAllNotificationsAsRead(token).enqueue(new Callback<com.example.wastereborn.model.ApiResponse>() {
            @Override
            public void onResponse(Call<com.example.wastereborn.model.ApiResponse> call, Response<com.example.wastereborn.model.ApiResponse> response) {
                if (response.isSuccessful()) {
                    for (Notification notification : notifications) {
                        notification.setIsRead(true);
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "All notifications marked as read", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.wastereborn.model.ApiResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to mark all as read", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
