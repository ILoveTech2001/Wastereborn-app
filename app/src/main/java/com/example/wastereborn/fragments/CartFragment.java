package com.example.wastereborn.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wastereborn.CartAdapter;
import com.example.wastereborn.CartManager;
import com.example.wastereborn.PaymentActivity;
import com.example.wastereborn.Product;
import com.example.wastereborn.R;
import com.example.wastereborn.utils.Constants;

import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView textTotal, textDelivery, textFinalAmount;
    private Button btnCheckout;
    private CartAdapter adapter;

    // Using centralized delivery fee constant

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = view.findViewById(R.id.recycler_cart);
        textTotal = view.findViewById(R.id.text_total_price);
        textDelivery = view.findViewById(R.id.text_delivery_fee);
        textFinalAmount = view.findViewById(R.id.text_final_amount);
        btnCheckout = view.findViewById(R.id.btn_checkout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Product> cartItems = CartManager.getInstance().getCartItems();
        adapter = new CartAdapter(cartItems, product -> {
            CartManager.getInstance().removeFromCart(product);
            updatePriceDetails(cartItems);
        });

        recyclerView.setAdapter(adapter);
        updatePriceDetails(cartItems);

        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(getContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Navigate to PaymentActivity
            Intent intent = new Intent(getContext(), PaymentActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void updatePriceDetails(List<Product> cartItems) {
        int total = CartManager.getInstance().getTotalCost();
        int finalAmount = total + (int) Constants.DELIVERY_FEE;

        textTotal.setText("Subtotal: FCFA " + total);
        textDelivery.setText("Delivery Fee: FCFA " + (int) Constants.DELIVERY_FEE);
        textFinalAmount.setText("Total: FCFA " + finalAmount);
    }
}
