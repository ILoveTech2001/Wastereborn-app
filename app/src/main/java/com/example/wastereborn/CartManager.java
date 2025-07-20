package com.example.wastereborn;

import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static CartManager instance;
    private final List<Product> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(Product product) {
        cartItems.add(product);
    }

    public void removeFromCart(Product product) {
        cartItems.remove(product);
    }

    public List<Product> getCartItems() {
        return new ArrayList<>(cartItems); // Return a copy to avoid external modification
    }

    public void clearCart() {
        cartItems.clear();
    }

    public int getTotalCost() {
        int total = 0;
        for (Product item : cartItems) {
            total += item.getPrice();
        }
        return total;
    }

    public double getTotalAmount() {
        double total = 0.0;
        for (Product item : cartItems) {
            total += item.getPrice();
        }
        return total;
    }
}
