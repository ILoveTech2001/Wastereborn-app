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
import com.example.wastereborn.api.ApiClient;
import com.example.wastereborn.model.OrderResponse;
import com.example.wastereborn.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersFragment extends Fragment {

    private RecyclerView recyclerOrders;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textEmptyState;
    private OrdersAdapter ordersAdapter;
    private List<OrderResponse> ordersList;
    private SessionManager sessionManager;

    public OrdersFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        // Initialize session manager
        sessionManager = new SessionManager(requireContext());

        // Initialize views
        recyclerOrders = view.findViewById(R.id.recycler_orders);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_orders);
        textEmptyState = view.findViewById(R.id.text_empty_orders);

        // Setup RecyclerView
        recyclerOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        ordersList = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(ordersList);
        recyclerOrders.setAdapter(ordersAdapter);

        // Setup SwipeRefreshLayout
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(this::loadOrders);
        }

        // Load orders
        loadOrders();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh orders when fragment becomes visible (e.g., after payment)
        System.out.println("📱 OrdersFragment: onResume() - Refreshing orders");
        loadOrders();
    }

    private void loadOrders() {
        if (!sessionManager.isLoggedIn()) {
            showEmptyState("Please login to view your orders");
            return;
        }

        setLoading(true);

        ApiClient.getApiService().getUserOrders(sessionManager.getAuthHeader())
                .enqueue(new Callback<List<Object>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Object>> call, @NonNull Response<List<Object>> response) {
                        setLoading(false);

                        if (response.isSuccessful() && response.body() != null) {
                            List<Object> apiOrders = response.body();
                            ordersList.clear();

                            // Convert API orders to OrderResponse objects
                            try {
                                Gson gson = new Gson();
                                String jsonString = gson.toJson(apiOrders);
                                Type listType = new TypeToken<List<OrderResponse>>(){}.getType();
                                List<OrderResponse> realOrders = gson.fromJson(jsonString, listType);
                                ordersList.addAll(realOrders);

                                System.out.println("✅ Loaded " + ordersList.size() + " orders from API");
                            } catch (Exception e) {
                                System.err.println("❌ Error parsing orders: " + e.getMessage());
                                e.printStackTrace();
                            }

                            if (ordersList.isEmpty()) {
                                showEmptyState("No orders found. Start shopping to see your orders here!");
                            } else {
                                hideEmptyState();
                            }

                            ordersAdapter.notifyDataSetChanged();
                        } else {
                            System.err.println("❌ API Error: " + response.code() + " - " + response.message());
                            showEmptyState("Failed to load orders. Please try again.");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Object>> call, @NonNull Throwable t) {
                        setLoading(false);
                        System.err.println("❌ Network Error: " + t.getMessage());
                        showEmptyState("Network error. Please check your connection.");
                    }
                });
    }

    // Removed hardcoded sample orders - now using real API data

    private void setLoading(boolean isLoading) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(isLoading);
        }
    }

    private void showEmptyState(String message) {
        if (textEmptyState != null) {
            textEmptyState.setText(message);
            textEmptyState.setVisibility(View.VISIBLE);
        }
        recyclerOrders.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        if (textEmptyState != null) {
            textEmptyState.setVisibility(View.GONE);
        }
        recyclerOrders.setVisibility(View.VISIBLE);
    }

    // Simple OrderItem class for display
    public static class OrderItem {
        public String orderNumber;
        public String status;
        public String totalAmount;
        public String orderDate;
        public String itemCount;
    }

    // OrdersAdapter class for real API data
    public static class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {
        private List<OrderResponse> orders;

        public OrdersAdapter(List<OrderResponse> orders) {
            this.orders = orders;
        }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            OrderResponse order = orders.get(position);

            String primaryText = (order.getOrderNumber() != null ? order.getOrderNumber() : "Order #" + order.getId())
                    + " - " + order.getFormattedStatus();

            String secondaryText = order.getFormattedAmount() + " • " + order.getItemCount() + " • " + order.getFormattedDate();

            holder.textPrimary.setText(primaryText);
            holder.textSecondary.setText(secondaryText);
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        static class OrderViewHolder extends RecyclerView.ViewHolder {
            TextView textPrimary, textSecondary;

            OrderViewHolder(View itemView) {
                super(itemView);
                textPrimary = itemView.findViewById(android.R.id.text1);
                textSecondary = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}
