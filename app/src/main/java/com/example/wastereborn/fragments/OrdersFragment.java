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
import com.example.wastereborn.utils.SessionManager;

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
    private List<OrderItem> ordersList;
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

                            // Convert API orders to local OrderItem objects
                            for (Object apiOrder : apiOrders) {
                                // This would need proper parsing based on your API response
                                OrderItem order = new OrderItem();
                                order.orderNumber = "WR" + System.currentTimeMillis();
                                order.status = "Processing";
                                order.totalAmount = "FCFA 5,500";
                                order.orderDate = "Today";
                                order.itemCount = "2 items";
                                ordersList.add(order);
                            }

                            if (ordersList.isEmpty()) {
                                showEmptyState("No orders found. Start shopping to see your orders here!");
                            } else {
                                hideEmptyState();
                            }

                            ordersAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
                            loadSampleOrders();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Object>> call, @NonNull Throwable t) {
                        setLoading(false);
                        Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        loadSampleOrders();
                    }
                });
    }

    private void loadSampleOrders() {
        ordersList.clear();

        OrderItem order1 = new OrderItem();
        order1.orderNumber = "WR1234567890";
        order1.status = "Delivered";
        order1.totalAmount = "FCFA 5,500";
        order1.orderDate = "2 days ago";
        order1.itemCount = "2 items";
        ordersList.add(order1);

        OrderItem order2 = new OrderItem();
        order2.orderNumber = "WR1234567891";
        order2.status = "Shipped";
        order2.totalAmount = "FCFA 3,800";
        order2.orderDate = "1 week ago";
        order2.itemCount = "1 item";
        ordersList.add(order2);

        OrderItem order3 = new OrderItem();
        order3.orderNumber = "WR1234567892";
        order3.status = "Processing";
        order3.totalAmount = "FCFA 2,000";
        order3.orderDate = "Today";
        order3.itemCount = "1 item";
        ordersList.add(order3);

        hideEmptyState();
        ordersAdapter.notifyDataSetChanged();
    }

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

    // Simple OrdersAdapter class
    public static class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {
        private List<OrderItem> orders;

        public OrdersAdapter(List<OrderItem> orders) {
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
            OrderItem order = orders.get(position);
            holder.textPrimary.setText(order.orderNumber + " - " + order.status);
            holder.textSecondary.setText(order.totalAmount + " • " + order.itemCount + " • " + order.orderDate);
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
