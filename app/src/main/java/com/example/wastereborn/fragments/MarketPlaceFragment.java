package com.example.wastereborn.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;

import com.example.wastereborn.Product;
import com.example.wastereborn.ProductAdapter;
import com.example.wastereborn.ProductDetailActivity;
import com.example.wastereborn.R;
import com.example.wastereborn.api.ApiClient;
import com.example.wastereborn.api.ApiService;
import com.example.wastereborn.model.ProductResponse;
import com.example.wastereborn.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarketPlaceFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private String currentCategory = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_market_place, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_marketplace);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize SwipeRefreshLayout if it exists in the layout
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(this::loadProducts);
        }

        // Initialize product list and adapter
        productList = new ArrayList<>();
        adapter = new ProductAdapter(productList);
        adapter.setOnProductClickListener(product -> {
            // Convert Product to ProductResponse and open detail activity
            ProductResponse productResponse = convertToProductResponse(product);
            Intent intent = new Intent(getContext(), ProductDetailActivity.class);
            intent.putExtra("product", productResponse);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // Load products from API
        loadProducts();

        // Setup category filter buttons
        setupCategoryButtons(view);

        // ✅ Cart Icon - Navigate to CartFragment
        ImageButton iconCart = view.findViewById(R.id.icon_cart);
        iconCart.setOnClickListener(v -> {
            Fragment cartFragment = new CartFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, cartFragment) // Replace with your actual container ID
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void setupCategoryButtons(View view) {
        View.OnClickListener listener = v -> {
            String selectedCategory = ((Button) v).getText().toString();
            currentCategory = selectedCategory;
            filterProductsByCategory(selectedCategory);
        };

        view.findViewById(R.id.category_all).setOnClickListener(listener);
        view.findViewById(R.id.category_electronics).setOnClickListener(listener);
        view.findViewById(R.id.category_plastic).setOnClickListener(listener);
        view.findViewById(R.id.category_shoes).setOnClickListener(listener);
        view.findViewById(R.id.category_utensils).setOnClickListener(listener);
        view.findViewById(R.id.category_books).setOnClickListener(listener);
    }

    private void loadProducts() {
        setLoading(true);

        ApiClient.getApiService().getAllProducts().enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductResponse>> call, @NonNull Response<List<ProductResponse>> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<ProductResponse> apiProducts = response.body();
                    productList.clear();

                    // Convert API products to local Product objects
                    for (ProductResponse apiProduct : apiProducts) {
                        Product product = new Product(
                            apiProduct.getName(),
                            apiProduct.getCategory(),
                            R.mipmap.sample_product, // Default image for now
                            apiProduct.getPrice(),
                            false
                        );
                        productList.add(product);
                    }

                    adapter.notifyDataSetChanged();
                    filterProductsByCategory(currentCategory);
                } else {
                    Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductResponse>> call, @NonNull Throwable t) {
                setLoading(false);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                // Load sample data as fallback
                loadSampleData();
            }
        });
    }

    private void filterProductsByCategory(String category) {
        adapter.updateFilter(category);
    }

    private void setLoading(boolean isLoading) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(isLoading);
        }
    }

    private void loadSampleData() {
        productList.clear();
        productList.add(new Product("Recycled Tote Bag", "Bags", R.mipmap.sample_product, 2000, false));
        productList.add(new Product("Eco-friendly Lamp", "Electronics", R.mipmap.sample_product_foreground, 3500, false));
        productList.add(new Product("Plastic Chair", "Plastic", R.mipmap.sample_bag_image, 5000, false));
        productList.add(new Product("Used Books Set", "Books", R.mipmap.sample_bag_image, 1000, false));
        productList.add(new Product("Vintage Shoes", "Shoes", R.mipmap.sample_product_foreground, 2500, false));
        productList.add(new Product("Utensil Set", "Utensils", R.mipmap.sample_product, 3000, false));

        adapter.notifyDataSetChanged();
        filterProductsByCategory(currentCategory);
    }

    private ProductResponse convertToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(Long.valueOf(product.getId()));
        response.setName(product.getName());
        response.setDescription("High-quality recycled product");
        response.setPrice(product.getPrice());
        response.setStockQuantity(10); // Default stock
        response.setIsAvailable(true);
        response.setAverageRating(4.5);
        response.setReviewCount(25);
        return response;
    }
}
