package com.example.wastereborn;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wastereborn.api.ApiClient;
import com.example.wastereborn.api.ApiService;
import com.example.wastereborn.model.Review;
import com.example.wastereborn.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteReviewActivity extends AppCompatActivity {
    
    private TextView productNameText;
    private RatingBar ratingBar;
    private EditText commentEditText;
    private Button submitButton;
    
    private Long productId;
    private String productName;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
        
        initViews();
        setupData();
        setupClickListeners();
    }

    private void initViews() {
        productNameText = findViewById(R.id.text_product_name);
        ratingBar = findViewById(R.id.rating_bar);
        commentEditText = findViewById(R.id.edit_comment);
        submitButton = findViewById(R.id.btn_submit);
        
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    private void setupData() {
        productId = getIntent().getLongExtra("product_id", 0);
        productName = getIntent().getStringExtra("product_name");
        
        if (productName != null) {
            productNameText.setText("Review for: " + productName);
        }
    }

    private void setupClickListeners() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        submitButton.setOnClickListener(v -> submitReview());
    }

    private void submitReview() {
        float rating = ratingBar.getRating();
        String comment = commentEditText.getText().toString().trim();
        
        if (rating == 0) {
            Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (comment.isEmpty()) {
            Toast.makeText(this, "Please write a comment", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create review object
        Review review = new Review();
        review.setProductId(productId);
        review.setRating((int) rating);
        review.setComment(comment);
        
        // Submit review
        submitButton.setEnabled(false);
        submitButton.setText("Submitting...");
        
        String token = "Bearer " + sessionManager.getToken();
        
        apiService.createReview(token, review).enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                submitButton.setEnabled(true);
                submitButton.setText("Submit Review");
                
                if (response.isSuccessful()) {
                    Toast.makeText(WriteReviewActivity.this, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(WriteReviewActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                submitButton.setEnabled(true);
                submitButton.setText("Submit Review");
                Toast.makeText(WriteReviewActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
