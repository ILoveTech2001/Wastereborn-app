package com.example.wastereborn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.wastereborn.api.ApiClient;
import com.example.wastereborn.api.ApiService;
import com.example.wastereborn.model.ApiResponse;
import com.example.wastereborn.utils.Constants;
import com.example.wastereborn.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvTotalAmount;
    private EditText etDeliveryAddress, etPhoneNumber;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbMTN, rbOrange, rbPoints, rbCashOnDelivery;
    private Button btnProceedPayment;
    private ProgressBar progressBar;
    private LinearLayout llPhoneNumber;
    private TextView tvPointsBalance, tvPointsRequired;

    private double totalAmount;
    private List<com.example.wastereborn.Product> cartItems;
    private String selectedPaymentMethod = "MTN_MOBILE_MONEY";
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        sessionManager = new SessionManager(this);
        initViews();
        setupData();
        setupListeners();
    }

    private void initViews() {
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        etDeliveryAddress = findViewById(R.id.et_delivery_address);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        rgPaymentMethod = findViewById(R.id.rg_payment_method);
        rbMTN = findViewById(R.id.rb_mtn);
        rbOrange = findViewById(R.id.rb_orange);
        rbPoints = findViewById(R.id.rb_points);
        rbCashOnDelivery = findViewById(R.id.rb_cash_on_delivery);
        btnProceedPayment = findViewById(R.id.btn_proceed_payment);
        progressBar = findViewById(R.id.progress_bar);
        llPhoneNumber = findViewById(R.id.ll_phone_number);
        tvPointsBalance = findViewById(R.id.tv_points_balance);
        tvPointsRequired = findViewById(R.id.tv_points_required);
    }

    private void setupData() {
        // Get cart items and calculate total
        cartItems = com.example.wastereborn.CartManager.getInstance().getCartItems();
        double cartTotal = com.example.wastereborn.CartManager.getInstance().getTotalAmount();

        System.out.println("🛒 Cart Setup:");
        System.out.println("   - Cart Items Count: " + (cartItems != null ? cartItems.size() : 0));
        System.out.println("   - Cart Total: " + cartTotal);

        if (cartItems != null) {
            for (com.example.wastereborn.Product item : cartItems) {
                System.out.println("   - Item: " + item.getName() + " - FCFA " + item.getPrice());
            }
        }

        totalAmount = cartTotal;

        // Add delivery fee (using consistent value)
        totalAmount += Constants.DELIVERY_FEE;

        System.out.println("💰 Final Total Amount: " + totalAmount + " (Cart: " + cartTotal + " + Delivery: " + Constants.DELIVERY_FEE + ")");

        tvTotalAmount.setText(String.format("%.0f FCFA", totalAmount));
        
        // Calculate points required
        int pointsRequired = (int) (totalAmount / Constants.POINTS_TO_FCFA_RATIO);
        tvPointsRequired.setText("Points required: " + pointsRequired);
        
        // TODO: Get user's actual points balance from API
        tvPointsBalance.setText("Your points: 150"); // Placeholder
    }

    private void setupListeners() {
        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_mtn) {
                selectedPaymentMethod = "MOBILE_MONEY";
                llPhoneNumber.setVisibility(View.VISIBLE);
                etPhoneNumber.setHint("MTN Mobile Money Number");
            } else if (checkedId == R.id.rb_orange) {
                selectedPaymentMethod = "ORANGE_MONEY";
                llPhoneNumber.setVisibility(View.VISIBLE);
                etPhoneNumber.setHint("Orange Money Number");
            } else if (checkedId == R.id.rb_points) {
                selectedPaymentMethod = "POINTS";
                llPhoneNumber.setVisibility(View.GONE);
            } else if (checkedId == R.id.rb_cash_on_delivery) {
                selectedPaymentMethod = "CASH_ON_DELIVERY";
                llPhoneNumber.setVisibility(View.GONE);
            }
        });

        btnProceedPayment.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        if (!validateInputs()) {
            return;
        }

        setLoading(true);

        // First create an order, then process payment
        createOrder();
    }

    private boolean validateInputs() {
        String address = etDeliveryAddress.getText().toString().trim();
        if (address.isEmpty()) {
            etDeliveryAddress.setError("Delivery address is required");
            return false;
        }

        if ((selectedPaymentMethod.equals("MOBILE_MONEY") || selectedPaymentMethod.equals("ORANGE_MONEY"))) {
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            if (phoneNumber.isEmpty()) {
                etPhoneNumber.setError("Phone number is required");
                return false;
            }
            if (!isValidPhoneNumber(phoneNumber)) {
                etPhoneNumber.setError("Please enter a valid phone number");
                return false;
            }
        }

        return true;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Basic validation for Cameroon phone numbers
        return phoneNumber.matches("^(6[5-9]\\d{7}|2[0-9]{8})$");
    }

    private void createOrder() {
        System.out.println("🔄 Starting order creation process...");

        if (!sessionManager.isLoggedIn()) {
            System.err.println("❌ User not logged in");
            showPaymentResult(false, "Please login to place an order", 0L);
            return;
        }

        System.out.println("✅ User is logged in: " + sessionManager.getUserEmail());

        // Create order request
        Map<String, Object> orderRequest = new HashMap<>();
        String deliveryAddress = etDeliveryAddress.getText().toString().trim();
        String deliveryPhone = etPhoneNumber.getText().toString().trim();

        orderRequest.put("deliveryAddress", deliveryAddress);
        orderRequest.put("deliveryCity", "Yaoundé"); // Default city
        orderRequest.put("deliveryPhone", deliveryPhone);
        orderRequest.put("paymentMethod", selectedPaymentMethod);
        orderRequest.put("totalAmount", totalAmount);

        System.out.println("📦 Order Details:");
        System.out.println("   - Address: " + deliveryAddress);
        System.out.println("   - Phone: " + deliveryPhone);
        System.out.println("   - Payment Method: " + selectedPaymentMethod);
        System.out.println("   - Total Amount: " + totalAmount);
        System.out.println("   - Cart Items Count: " + (cartItems != null ? cartItems.size() : 0));

        // Add cart items to order
        List<Map<String, Object>> orderItems = new ArrayList<>();
        if (cartItems != null && !cartItems.isEmpty()) {
            for (com.example.wastereborn.Product product : cartItems) {
                Map<String, Object> item = new HashMap<>();
                item.put("productId", product.getName()); // Using name as ID for now
                item.put("quantity", 1);
                item.put("unitPrice", product.getPrice());
                orderItems.add(item);
                System.out.println("   - Item: " + product.getName() + " (FCFA " + product.getPrice() + ")");
            }
        } else {
            System.err.println("⚠️ No cart items found!");
        }
        orderRequest.put("orderItems", orderItems);

        // Create order via API
        System.out.println("🌐 Making API call to create order...");
        String authHeader = sessionManager.getAuthHeader();
        System.out.println("🔑 Auth Header: " + authHeader);
        System.out.println("🔑 User Email: " + sessionManager.getUserEmail());
        System.out.println("🔑 Is Logged In: " + sessionManager.isLoggedIn());

        if (authHeader == null) {
            System.err.println("❌ No auth header found! User might not be logged in properly.");
            showPaymentResult(false, "Authentication error. Please login again.", 0L);
            return;
        }

        ApiClient.getApiService().createOrder(sessionManager.getAuthHeader(), orderRequest)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        System.out.println("📡 API Response received:");
                        System.out.println("   - Response Code: " + response.code());
                        System.out.println("   - Response Message: " + response.message());

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse apiResponse = response.body();
                            System.out.println("   - API Response Success: " + apiResponse.isSuccess());
                            System.out.println("   - API Response Message: " + apiResponse.getMessage());
                            System.out.println("   - API Response Data: " + apiResponse.getData());

                            if (apiResponse.isSuccess()) {
                                // Extract order ID from response
                                Long orderId = extractOrderId(apiResponse);
                                System.out.println("✅ Order created successfully with ID: " + orderId);
                                processPaymentForOrder(orderId);
                            } else {
                                String errorMsg = "Failed to create order: " + apiResponse.getMessage();
                                System.err.println("❌ " + errorMsg);
                                showPaymentResult(false, errorMsg, 0L);
                            }
                        } else {
                            String errorMsg = "HTTP " + response.code() + ": " + response.message();
                            System.err.println("❌ Order creation failed: " + errorMsg);

                            // Try to get error body
                            try {
                                if (response.errorBody() != null) {
                                    String errorBody = response.errorBody().string();
                                    System.err.println("❌ Error Body: " + errorBody);
                                    errorMsg += " - " + errorBody;
                                }
                            } catch (Exception e) {
                                System.err.println("❌ Could not read error body: " + e.getMessage());
                            }

                            showPaymentResult(false, "Failed to create order: " + errorMsg, 0L);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        String errorMsg = "Network error: " + t.getMessage();
                        System.err.println("❌ " + errorMsg);
                        t.printStackTrace();
                        showPaymentResult(false, errorMsg, 0L);
                    }
                });
    }

    private Long extractOrderId(ApiResponse response) {
        try {
            // Try to extract order ID from response data
            if (response.getData() instanceof Map) {
                Map<String, Object> data = (Map<String, Object>) response.getData();
                Object orderIdObj = data.get("orderId");
                if (orderIdObj instanceof Number) {
                    return ((Number) orderIdObj).longValue();
                }
            }
            // Fallback to timestamp-based ID
            return System.currentTimeMillis();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }

    // Removed simulation - now using real API order creation

    private void processPaymentForOrder(Long orderId) {
        if (selectedPaymentMethod.equals("MOBILE_MONEY")) {
            processMTNPayment(orderId);
        } else if (selectedPaymentMethod.equals("POINTS")) {
            processPointsPayment(orderId);
        } else if (selectedPaymentMethod.equals("CASH_ON_DELIVERY")) {
            processCashOnDelivery(orderId);
        } else {
            // Handle other payment methods
            showPaymentResult(true, "Payment method not yet implemented", orderId);
        }
    }

    private void processMTNPayment(Long orderId) {
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        
        // Simulate MTN Mobile Money payment process
        showMTNPaymentDialog(phoneNumber, () -> {
            // Simulate payment processing
            new android.os.Handler().postDelayed(() -> {
                // Simulate 90% success rate
                boolean success = Math.random() > 0.1;
                
                if (success) {
                    showPaymentResult(true, 
                        "Payment successful! Your order will be delivered within 2-3 business days.", 
                        orderId);
                    clearCart();
                } else {
                    showPaymentResult(false, 
                        "Payment failed. Please check your phone number and try again.", 
                        orderId);
                }
                setLoading(false);
            }, 3000);
        });
    }

    private void processPointsPayment(Long orderId) {
        int pointsRequired = (int) (totalAmount / 10);
        
        // TODO: Check actual user points balance from API
        int userPoints = 150; // Placeholder
        
        if (userPoints < pointsRequired) {
            showPaymentResult(false, 
                "Insufficient points. You need " + pointsRequired + " points but only have " + userPoints + ".", 
                orderId);
            setLoading(false);
            return;
        }

        // Simulate points payment processing
        new android.os.Handler().postDelayed(() -> {
            showPaymentResult(true, 
                "Payment successful with points! " + pointsRequired + " points deducted. Your order will be delivered within 2-3 business days.", 
                orderId);
            clearCart();
            setLoading(false);
        }, 2000);
    }

    private void processCashOnDelivery(Long orderId) {
        // Cash on delivery doesn't require immediate payment processing
        new android.os.Handler().postDelayed(() -> {
            showPaymentResult(true, 
                "Order confirmed! You will pay " + String.format("%.0f FCFA", totalAmount) + " upon delivery. Expected delivery: 2-3 business days.", 
                orderId);
            clearCart();
            setLoading(false);
        }, 1000);
    }

    private void showMTNPaymentDialog(String phoneNumber, Runnable onConfirm) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("MTN Mobile Money Payment")
                .setMessage("You will receive a payment request on " + phoneNumber + " for " + String.format("%.0f FCFA", totalAmount) + ". Please confirm the payment on your phone.")
                .setPositiveButton("I've Confirmed", (dialog, which) -> {
                    onConfirm.run();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    setLoading(false);
                })
                .setCancelable(false)
                .show();
    }

    private void showPaymentResult(boolean success, String message, Long orderId) {
        Intent intent = new Intent(this, PaymentResultActivity.class);
        intent.putExtra("success", success);
        intent.putExtra("message", message);
        intent.putExtra("orderId", orderId);
        intent.putExtra("amount", totalAmount);
        startActivity(intent);
        finish();
    }

    private void clearCart() {
        com.example.wastereborn.CartManager.getInstance().clearCart();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnProceedPayment.setEnabled(!loading);
        btnProceedPayment.setText(loading ? "Processing..." : "Proceed with Payment");
    }
}
