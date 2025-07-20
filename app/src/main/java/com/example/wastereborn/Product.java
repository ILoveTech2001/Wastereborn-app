package com.example.wastereborn;

public class Product {
    private int id;
    private String name;
    private String category;
    private int imageResId;
    private String imageUrl;
    private double price;
    private boolean liked;

    // Constructor with image resource ID (for backward compatibility)
    public Product(int id, String name, String category, int imageResId, double price, boolean liked) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.imageResId = imageResId;
        this.imageUrl = null;
        this.price = price;
        this.liked = liked;
    }

    // Constructor with image URL (for API products)
    public Product(String name, String category, String imageUrl, double price, boolean liked) {
        this.name = name;
        this.category = category;
        this.imageResId = 0;
        this.imageUrl = imageUrl;
        this.price = price;
        this.liked = liked;
    }

    // Constructor without id for backward compatibility
    public Product(String name, String category, int imageResId, double price, boolean liked) {
        this(0, name, category, imageResId, price, liked);
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getImageResId() { return imageResId; }
    public String getImageUrl() { return imageUrl; }
    public double getPrice() { return price; }
    public boolean isLiked() { return liked; }

    // Helper method to check if product has image URL
    public boolean hasImageUrl() { return imageUrl != null && !imageUrl.isEmpty(); }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setLiked(boolean liked) { this.liked = liked; }
}
