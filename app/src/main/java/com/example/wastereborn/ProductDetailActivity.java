package com.example.wastereborn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wastereborn.adapters.ReviewAdapter;
import com.example.wastereborn.api.ApiClient;
import com.example.wastereborn.api.ApiService;
import com.example.wastereborn.model.ProductResponse;
import com.example.wastereborn.model.Review;
import com.example.wastereborn.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    
    private ImageView productImage;
    private TextView productName, productDescription, productPrice, productCategory;
    private TextView stockStatus, averageRating, reviewCount;
    private RatingBar ratingBar;
    private Button addToCartButton, writeReviewButton;
    private RecyclerView reviewsRecyclerView;
    
    private ReviewAdapter reviewAdapter;
    private List<Review> reviews;
    private ProductResponse product;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        
        initViews();
        setupRecyclerView();
        
        // Get product from intent
        product = (ProductResponse) getIntent().getSerializableExtra("product");
        if (product != null) {
            displayProductDetails();
            loadProductReviews();
        } else {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        productDescription = findViewById(R.id.product_description);
        productPrice = findViewById(R.id.product_price);
        productCategory = findViewById(R.id.product_category);
        stockStatus = findViewById(R.id.stock_status);
        averageRating = findViewById(R.id.average_rating);
        reviewCount = findViewById(R.id.review_count);
        ratingBar = findViewById(R.id.rating_bar);
        addToCartButton = findViewById(R.id.btn_add_to_cart);
        writeReviewButton = findViewById(R.id.btn_write_review);
        reviewsRecyclerView = findViewById(R.id.recycler_reviews);
        
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient().create(ApiService.class);
        
        setupClickListeners();
    }

    private void setupClickListeners() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        addToCartButton.setOnClickListener(v -> {
            // Add to cart functionality
            Toast.makeText(this, "Added to cart!", Toast.LENGTH_SHORT).show();
        });
        
        writeReviewButton.setOnClickListener(v -> {
            // Open write review dialog/activity
            openWriteReviewDialog();
        });
    }

    private void setupRecyclerView() {
        reviews = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviews);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewsRecyclerView.setAdapter(reviewAdapter);
    }

    private void displayProductDetails() {
        productName.setText(product.getName());
        productDescription.setText(product.getDescription());
        productPrice.setText("$" + product.getPrice());
        
        // Load product image
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_product_placeholder)
                    .into(productImage);
        }
        
        // Stock status
        if (product.getStockQuantity() != null && product.getStockQuantity() > 0) {
            stockStatus.setText("In Stock (" + product.getStockQuantity() + " available)");
            stockStatus.setTextColor(getResources().getColor(R.color.success));
            addToCartButton.setEnabled(true);
        } else {
            stockStatus.setText("Out of Stock");
            stockStatus.setTextColor(getResources().getColor(R.color.error));
            addToCartButton.setEnabled(false);
        }
        
        // Rating
        if (product.getAverageRating() != null) {
            ratingBar.setRating(product.getAverageRating().floatValue());
            averageRating.setText(String.format("%.1f", product.getAverageRating()));
        }
        
        if (product.getReviewCount() != null) {
            reviewCount.setText("(" + product.getReviewCount() + " reviews)");
        }
    }

    private void loadProductReviews() {
        apiService.getProductReviews(product.getId()).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reviews.clear();
                    reviews.addAll(response.body());
                    reviewAdapter.notifyDataSetChanged();
                    
                    // Update review count
                    reviewCount.setText("(" + reviews.size() + " reviews)");
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Failed to load reviews", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openWriteReviewDialog() {
        Intent intent = new Intent(this, WriteReviewActivity.class);
        intent.putExtra("product_id", product.getId());
        intent.putExtra("product_name", product.getName());
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Reload reviews after writing a new one
            loadProductReviews();
        }
    }
}
