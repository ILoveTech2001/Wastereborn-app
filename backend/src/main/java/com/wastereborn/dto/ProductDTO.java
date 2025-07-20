package com.wastereborn.dto;

import com.wastereborn.model.Product;

public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private String categoryName;
    private Double price;
    private Integer pointsPrice;
    private String imageUrl;
    private Integer stockQuantity;
    private Boolean isAvailable;
    private Boolean isPointsRedeemable;
    private Double averageRating;
    private Integer reviewCount;
    private String createdAt;
    private String updatedAt;

    public ProductDTO() {}

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.categoryName = product.getCategory() != null ? product.getCategory().getName() : null;
        this.price = product.getPrice();
        this.pointsPrice = product.getPointsPrice();
        this.imageUrl = product.getImageUrl();
        this.stockQuantity = product.getStockQuantity();
        this.isAvailable = product.getIsAvailable();
        this.isPointsRedeemable = product.getIsPointsRedeemable();
        this.averageRating = product.getAverageRating();
        this.reviewCount = product.getReviewCount();
        this.createdAt = product.getCreatedAt() != null ? product.getCreatedAt().toString() : null;
        this.updatedAt = product.getUpdatedAt() != null ? product.getUpdatedAt().toString() : null;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getPointsPrice() { return pointsPrice; }
    public void setPointsPrice(Integer pointsPrice) { this.pointsPrice = pointsPrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public Boolean getIsPointsRedeemable() { return isPointsRedeemable; }
    public void setIsPointsRedeemable(Boolean isPointsRedeemable) { this.isPointsRedeemable = isPointsRedeemable; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
