package com.example.wastereborn.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wastereborn.Product;
import com.example.wastereborn.ProductAdapter;
import com.example.wastereborn.R;

import java.util.ArrayList;
import java.util.List;

public class MarketPlaceFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_market_place, container, false);
        recyclerView = view.findViewById(R.id.recycler_marketplace);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Sample data
        productList = new ArrayList<>();
        productList.add(new Product("Recycled Tote Bag", "Bags", R.mipmap.sample_product, 2000, false));
        productList.add(new Product("Eco-friendly Lamp", "Electronics", R.mipmap.sample_product_foreground, 3500, false));
        productList.add(new Product("Plastic Chair", "Plastic Bucket", R.mipmap.sample_bag_image, 5000, false));
        productList.add(new Product("Used Books Set", "Books", R.mipmap.sample_bag_image, 1000, false));
        productList.add(new Product("Vintage Shoes", "Shoes", R.mipmap.sample_product_foreground, 2500, false));
        productList.add(new Product("Utensil Set", "Utensils", R.mipmap.sample_product, 3000, false));

        adapter = new ProductAdapter(productList);
        recyclerView.setAdapter(adapter);

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
            adapter.updateFilter(selectedCategory); // ✅ Use the adapter method directly
        };

        view.findViewById(R.id.category_all).setOnClickListener(listener);
        view.findViewById(R.id.category_electronics).setOnClickListener(listener);
        view.findViewById(R.id.category_plastic).setOnClickListener(listener);
        view.findViewById(R.id.category_shoes).setOnClickListener(listener);
        view.findViewById(R.id.category_utensils).setOnClickListener(listener);
        view.findViewById(R.id.category_books).setOnClickListener(listener);
    }
}
