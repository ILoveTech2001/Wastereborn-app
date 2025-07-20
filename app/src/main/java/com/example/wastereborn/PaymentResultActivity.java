package com.example.wastereborn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentResultActivity extends AppCompatActivity {

    private ImageView ivResultIcon;
    private TextView tvResultTitle, tvResultMessage, tvOrderDetails;
    private Button btnBackToHome, btnViewOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);

        initViews();
        setupResult();
        setupListeners();
    }

    private void initViews() {
        ivResultIcon = findViewById(R.id.iv_result_icon);
        tvResultTitle = findViewById(R.id.tv_result_title);
        tvResultMessage = findViewById(R.id.tv_result_message);
        tvOrderDetails = findViewById(R.id.tv_order_details);
        btnBackToHome = findViewById(R.id.btn_back_to_home);
        btnViewOrders = findViewById(R.id.btn_view_orders);
    }

    private void setupResult() {
        boolean success = getIntent().getBooleanExtra("success", false);
        String message = getIntent().getStringExtra("message");
        Long orderId = getIntent().getLongExtra("orderId", 0);
        double amount = getIntent().getDoubleExtra("amount", 0);

        if (success) {
            ivResultIcon.setImageResource(R.drawable.ic_check_circle);
            ivResultIcon.setColorFilter(getResources().getColor(R.color.primary_green, null));
            tvResultTitle.setText("Payment Successful!");
            tvResultTitle.setTextColor(getResources().getColor(R.color.primary_green, null));
            
            tvOrderDetails.setText(String.format(
                "Order ID: #%d\nAmount: %.0f FCFA\nStatus: Confirmed", 
                orderId, amount
            ));
            
            btnViewOrders.setVisibility(android.view.View.VISIBLE);
        } else {
            ivResultIcon.setImageResource(R.drawable.ic_error);
            ivResultIcon.setColorFilter(getResources().getColor(android.R.color.holo_red_dark, null));
            tvResultTitle.setText("Payment Failed");
            tvResultTitle.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
            
            tvOrderDetails.setText(String.format(
                "Order ID: #%d\nAmount: %.0f FCFA\nStatus: Payment Failed", 
                orderId, amount
            ));
            
            btnViewOrders.setVisibility(android.view.View.GONE);
        }

        tvResultMessage.setText(message);
    }

    private void setupListeners() {
        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnViewOrders.setOnClickListener(v -> {
            // TODO: Navigate to orders fragment/activity
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("navigate_to", "orders");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to payment screen
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
