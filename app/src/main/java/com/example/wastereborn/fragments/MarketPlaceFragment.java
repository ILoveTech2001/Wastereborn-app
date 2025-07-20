package com.example.wastereborn.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

        // Load categories first, then products
        loadCategories(view);
        loadProducts();

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

    private void debugRecyclerView() {
        System.out.println("🔍 DEBUG: RecyclerView setup check");
        System.out.println("🔍 DEBUG: RecyclerView null? " + (recyclerView == null));
        System.out.println("🔍 DEBUG: Adapter null? " + (adapter == null));
        System.out.println("🔍 DEBUG: ProductList null? " + (productList == null));
        if (productList != null) {
            System.out.println("🔍 DEBUG: ProductList size: " + productList.size());
        }
        if (adapter != null) {
            System.out.println("🔍 DEBUG: Adapter item count: " + adapter.getItemCount());
        }
        if (recyclerView != null) {
            System.out.println("🔍 DEBUG: RecyclerView visibility: " + recyclerView.getVisibility());
            System.out.println("🔍 DEBUG: RecyclerView has layout manager? " + (recyclerView.getLayoutManager() != null));
        }
    }

    private void loadCategories(View view) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<String>> call = apiService.getCategories();

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setupDynamicCategoryChips(view, response.body());
                } else {
                    // Fallback to existing static categories
                    setupStaticCategoryButtons(view);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                // Fallback to existing static categories
                setupStaticCategoryButtons(view);
            }
        });
    }

    private void setupDynamicCategoryChips(View view, List<String> categories) {
        LinearLayout container = view.findViewById(R.id.categories_container);

        // Clear existing dynamic categories (keep only "All" button)
        // Remove all views except the first one (All button)
        while (container.getChildCount() > 1) {
            container.removeViewAt(container.getChildCount() - 1);
        }

        // Setup "All" button click listener
        Button allButton = view.findViewById(R.id.category_all);
        allButton.setOnClickListener(v -> {
            currentCategory = "All";
            filterProductsByCategory("All");
            updateCategorySelection(container, allButton);
        });

        // Add dynamic category chips
        for (String category : categories) {
            addCategoryChip(container, category);
        }
    }

    private void setupStaticCategoryButtons(View view) {
        View.OnClickListener listener = v -> {
            String selectedCategory = ((Button) v).getText().toString();
            currentCategory = selectedCategory;
            filterProductsByCategory(selectedCategory);
            updateCategorySelection((LinearLayout) view.findViewById(R.id.categories_container), (Button) v);
        };

        // Setup existing static buttons
        Button allBtn = view.findViewById(R.id.category_all);
        if (allBtn != null) allBtn.setOnClickListener(listener);

        Button electronicsBtn = view.findViewById(R.id.category_electronics);
        if (electronicsBtn != null) electronicsBtn.setOnClickListener(listener);

        Button plasticBtn = view.findViewById(R.id.category_plastic);
        if (plasticBtn != null) plasticBtn.setOnClickListener(listener);

        Button shoesBtn = view.findViewById(R.id.category_shoes);
        if (shoesBtn != null) shoesBtn.setOnClickListener(listener);
    }

    private void addCategoryChip(LinearLayout container, String categoryName) {
        // Create CardView for the chip
        androidx.cardview.widget.CardView cardView = new androidx.cardview.widget.CardView(getContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            (int) (40 * getResources().getDisplayMetrics().density) // 40dp in pixels
        );
        cardParams.setMarginEnd((int) (12 * getResources().getDisplayMetrics().density)); // 12dp margin
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(20 * getResources().getDisplayMetrics().density); // 20dp radius
        cardView.setCardElevation(4 * getResources().getDisplayMetrics().density); // 4dp elevation
        cardView.setCardBackgroundColor(getResources().getColor(android.R.color.white, null));

        // Create Button for the chip
        Button button = new Button(getContext());
        androidx.cardview.widget.CardView.LayoutParams buttonParams =
            new androidx.cardview.widget.CardView.LayoutParams(
                androidx.cardview.widget.CardView.LayoutParams.WRAP_CONTENT,
                androidx.cardview.widget.CardView.LayoutParams.MATCH_PARENT
            );
        button.setLayoutParams(buttonParams);
        button.setText(categoryName);
        button.setTextColor(getResources().getColor(R.color.text_primary, null));
        button.setTextSize(14);
        button.setBackground(null);
        button.setPadding(
            (int) (20 * getResources().getDisplayMetrics().density), 0,
            (int) (20 * getResources().getDisplayMetrics().density), 0
        );
        button.setMinWidth(0);
        button.setMinHeight(0);

        // Set click listener
        button.setOnClickListener(v -> {
            currentCategory = categoryName;
            filterProductsByCategory(categoryName);
            updateCategorySelection(container, button);
        });

        cardView.addView(button);
        container.addView(cardView);
    }

    private void updateCategorySelection(LinearLayout container, Button selectedButton) {
        // Update visual selection state
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof androidx.cardview.widget.CardView) {
                androidx.cardview.widget.CardView cardView = (androidx.cardview.widget.CardView) child;
                Button button = (Button) cardView.getChildAt(0);

                if (button == selectedButton) {
                    // Selected state
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.primary_green, null));
                    button.setTextColor(getResources().getColor(android.R.color.white, null));
                } else {
                    // Unselected state
                    cardView.setCardBackgroundColor(getResources().getColor(android.R.color.white, null));
                    button.setTextColor(getResources().getColor(R.color.text_primary, null));
                }
            }
        }
    }

    private void loadProducts() {
        setLoading(true);

        System.out.println("📦 Starting to load products from API...");

        ApiClient.getApiService().getAllProducts().enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductResponse>> call, @NonNull Response<List<ProductResponse>> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<ProductResponse> apiProducts = response.body();
                    productList.clear();

                    System.out.println("📦 Loaded " + apiProducts.size() + " products from API");

                    // Convert API products to local Product objects
                    for (ProductResponse apiProduct : apiProducts) {
                        System.out.println("📦 Product: " + apiProduct.getName() +
                                         " | Category: " + apiProduct.getCategoryName() +
                                         " | Available: " + apiProduct.getIsAvailable());

                        Product product = new Product(
                            apiProduct.getName(),
                            apiProduct.getCategoryName(), // Use category name instead of category object
                            apiProduct.getImageUrl(), // Use actual image URL from API
                            apiProduct.getPrice(),
                            false
                        );

                        System.out.println("🖼️ Product: " + apiProduct.getName() +
                                         " | Image URL: " + apiProduct.getImageUrl());
                        productList.add(product);
                    }

                    // Update adapter with new product list
                    adapter.updateProductList(productList);

                    // Apply current category filter
                    filterProductsByCategory(currentCategory);

                    System.out.println("📦 Total products in list: " + productList.size());
                    System.out.println("📦 Current category filter: " + currentCategory);

                    // Debug RecyclerView setup
                    debugRecyclerView();

                    Toast.makeText(getContext(), "Loaded " + productList.size() + " products", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to load products: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductResponse>> call, @NonNull Throwable t) {
                setLoading(false);

                // Log detailed error information
                System.err.println("❌ Network error loading products: " + t.getClass().getSimpleName());
                System.err.println("❌ Error message: " + t.getMessage());
                if (t.getCause() != null) {
                    System.err.println("❌ Cause: " + t.getCause().getMessage());
                }
                t.printStackTrace();

                // Show longer toast with more details
                String errorMsg = "Network error: " + t.getClass().getSimpleName();
                if (t.getMessage() != null) {
                    errorMsg += " - " + t.getMessage();
                }
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();

                // Load sample data as fallback
                loadSampleData();
            }
        });
    }

    private void filterProductsByCategory(String category) {
        System.out.println("📦 Filtering products by category: " + category);
        System.out.println("📦 Total products before filter: " + productList.size());
        adapter.updateFilter(category);
        System.out.println("📦 Filter applied");
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
