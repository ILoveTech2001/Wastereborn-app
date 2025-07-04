package com.example.wastereborn;

public class Product {
    private String name;
    private String category;
    private int imageResId;
    private double price;
    private boolean liked;

    public Product(String name, String category, int imageResId, double price, boolean liked) {
        this.name = name;
        this.category = category;
        this.imageResId = imageResId;
        this.price = price;
        this.liked = liked;
    }

    // Getters
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getImageResId() { return imageResId; }
    public double getPrice() { return price; }
    public boolean isLiked() { return liked; }

    // Setters
    public void setLiked(boolean liked) { this.liked = liked; }
}
