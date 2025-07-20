package com.example.wastereborn.api;

import com.example.wastereborn.model.ApiResponse;
import com.example.wastereborn.model.Category;
import com.example.wastereborn.model.Driver;
import com.example.wastereborn.model.JwtResponse;
import com.example.wastereborn.model.LoginRequest;
import com.example.wastereborn.model.Notification;
import com.example.wastereborn.model.ProductResponse;
import com.example.wastereborn.model.Review;
import com.example.wastereborn.model.Reward;
import com.example.wastereborn.model.SignupRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    
    // Authentication endpoints
    @POST("auth/login")
    Call<JwtResponse> login(@Body LoginRequest loginRequest);
    
    @POST("auth/signup")
    Call<JwtResponse> signup(@Body SignupRequest signupRequest);
    
    @GET("auth/validate")
    Call<JwtResponse> validateToken(@Header("Authorization") String token);
    
    // Product endpoints
    @GET("products/public/all")
    Call<List<ProductResponse>> getAllProducts();
    
    @GET("products/public/categories")
    Call<List<String>> getCategories();
    
    @GET("products/public/category/{category}")
    Call<List<ProductResponse>> getProductsByCategory(@Path("category") String category);
    
    @GET("products/public/search")
    Call<List<ProductResponse>> searchProducts(@Query("keyword") String keyword);
    
    @GET("products/public/{id}")
    Call<ProductResponse> getProductById(@Path("id") Long id);
    
    @GET("products/points-redeemable")
    Call<List<ProductResponse>> getPointsRedeemableProducts(@Header("Authorization") String token);
    
    // User endpoints
    @GET("users/profile")
    Call<JwtResponse> getUserProfile(@Header("Authorization") String token);
    
    @PUT("users/profile")
    Call<JwtResponse> updateUserProfile(@Header("Authorization") String token, @Body SignupRequest updateRequest);
    
    // Pickup endpoints
    @POST("pickups")
    Call<ApiResponse> createPickupRequest(@Header("Authorization") String token, @Body Object pickupRequest);
    
    @GET("pickups/user")
    Call<List<Object>> getUserPickups(@Header("Authorization") String token);
    
    @GET("pickups/{id}")
    Call<Object> getPickupById(@Header("Authorization") String token, @Path("id") Long id);
    
    // Order endpoints
    @POST("orders")
    Call<ApiResponse> createOrder(@Header("Authorization") String token, @Body Object orderRequest);
    
    @GET("orders/my-orders")
    Call<List<Object>> getUserOrders(@Header("Authorization") String token);
    
    @GET("orders/{id}")
    Call<Object> getOrderById(@Header("Authorization") String token, @Path("id") Long id);
    
    // Admin endpoints
    @GET("admin/users")
    Call<List<JwtResponse>> getAllUsers(@Header("Authorization") String token);
    
    @GET("admin/orders")
    Call<List<Object>> getAllOrders(@Header("Authorization") String token);
    
    @GET("admin/pickups")
    Call<List<Object>> getAllPickups(@Header("Authorization") String token);
    
    @GET("admin/revenue")
    Call<Object> getRevenue(@Header("Authorization") String token);
    
    @POST("products")
    Call<ProductResponse> createProduct(@Header("Authorization") String token, @Body ProductResponse product);
    
    @PUT("products/{id}")
    Call<ProductResponse> updateProduct(@Header("Authorization") String token, @Path("id") Long id, @Body ProductResponse product);
    
    @DELETE("products/{id}")
    Call<ApiResponse> deleteProduct(@Header("Authorization") String token, @Path("id") Long id);

    // ========== NEW ENDPOINTS FOR ENHANCED FEATURES ==========

    // Category endpoints
    @GET("categories")
    Call<List<Category>> getCategories(@Header("Authorization") String token);

    @GET("categories/{id}")
    Call<Category> getCategoryById(@Header("Authorization") String token, @Path("id") Long id);

    // Review endpoints
    @GET("reviews/product/{productId}")
    Call<List<Review>> getProductReviews(@Path("productId") Long productId);

    @POST("reviews")
    Call<Review> createReview(@Header("Authorization") String token, @Body Review review);

    @PUT("reviews/{id}")
    Call<Review> updateReview(@Header("Authorization") String token, @Path("id") Long id, @Body Review review);

    @DELETE("reviews/{id}")
    Call<ApiResponse> deleteReview(@Header("Authorization") String token, @Path("id") Long id);

    // Notification endpoints
    @GET("notifications/user")
    Call<List<Notification>> getUserNotifications(@Header("Authorization") String token);

    @PUT("notifications/{id}/read")
    Call<Notification> markNotificationAsRead(@Header("Authorization") String token, @Path("id") Long id);

    @PUT("notifications/mark-all-read")
    Call<ApiResponse> markAllNotificationsAsRead(@Header("Authorization") String token);

    @GET("notifications/unread-count")
    Call<Map<String, Integer>> getUnreadNotificationCount(@Header("Authorization") String token);

    // Reward endpoints
    @GET("rewards/user")
    Call<List<Reward>> getUserRewards(@Header("Authorization") String token);

    @GET("rewards/available-points")
    Call<Map<String, Integer>> getAvailablePoints(@Header("Authorization") String token);

    @POST("rewards/redeem")
    Call<ApiResponse> redeemPoints(@Header("Authorization") String token, @Body Map<String, Object> redeemRequest);

    // Driver endpoints (for tracking)
    @GET("drivers/available")
    Call<List<Driver>> getAvailableDrivers(@Header("Authorization") String token);

    @GET("pickups/{id}/driver")
    Call<Driver> getPickupDriver(@Header("Authorization") String token, @Path("id") Long pickupId);

    // Enhanced product endpoints
    @GET("products/{id}/reviews")
    Call<List<Review>> getProductReviewsById(@Path("id") Long productId);

    @GET("products/category/{categoryId}")
    Call<List<ProductResponse>> getProductsByCategoryId(@Path("categoryId") Long categoryId);

    @GET("products/featured")
    Call<List<ProductResponse>> getFeaturedProducts();

    @GET("products/top-rated")
    Call<List<ProductResponse>> getTopRatedProducts();

    // Enhanced user endpoints
    @GET("users/profile/detailed")
    Call<JwtResponse> getDetailedUserProfile(@Header("Authorization") String token);

    @PUT("users/profile/image")
    Call<ApiResponse> updateProfileImage(@Header("Authorization") String token, @Body Map<String, String> imageData);

    // Dashboard endpoints
    @GET("dashboard/stats")
    Call<Map<String, Object>> getDashboardStats(@Header("Authorization") String token);

    @GET("dashboard/recent-activity")
    Call<List<Object>> getRecentActivity(@Header("Authorization") String token);

    // Search and filter endpoints
    @GET("products/search/advanced")
    Call<List<ProductResponse>> advancedProductSearch(
        @Query("keyword") String keyword,
        @Query("category") String category,
        @Query("minPrice") Double minPrice,
        @Query("maxPrice") Double maxPrice,
        @Query("sortBy") String sortBy
    );
}
