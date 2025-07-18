package com.example.wastereborn.model;

public class Reward {
    private Long id;
    private Long userId;
    private Integer points;
    private String type;
    private String description;
    private String referenceId;
    private String referenceType;
    private String earnedDate;
    private String expiresAt;
    private Boolean isRedeemed;
    private String redeemedAt;
    private String createdAt;

    public Reward() {}

    public Reward(Integer points, String type, String description) {
        this.points = points;
        this.type = type;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }

    public String getEarnedDate() { return earnedDate; }
    public void setEarnedDate(String earnedDate) { this.earnedDate = earnedDate; }

    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }

    public Boolean getIsRedeemed() { return isRedeemed; }
    public void setIsRedeemed(Boolean isRedeemed) { this.isRedeemed = isRedeemed; }

    public String getRedeemedAt() { return redeemedAt; }
    public void setRedeemedAt(String redeemedAt) { this.redeemedAt = redeemedAt; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    // Utility methods
    public boolean isValid() {
        return isRedeemed == null || !isRedeemed;
    }

    public String getTypeIcon() {
        switch (type != null ? type.toUpperCase() : "SIGNUP_BONUS") {
            case "SIGNUP_BONUS": return "🎁";
            case "PICKUP_COMPLETION": return "🚛";
            case "ORDER_PURCHASE": return "🛒";
            case "REFERRAL_BONUS": return "👥";
            case "REVIEW_BONUS": return "⭐";
            case "LOYALTY_BONUS": return "💎";
            case "SPECIAL_PROMOTION": return "🎉";
            case "RECYCLING_MILESTONE": return "🏆";
            default: return "🎁";
        }
    }

    public String getTypeDisplayName() {
        switch (type != null ? type.toUpperCase() : "SIGNUP_BONUS") {
            case "SIGNUP_BONUS": return "Welcome Bonus";
            case "PICKUP_COMPLETION": return "Pickup Completed";
            case "ORDER_PURCHASE": return "Order Purchase";
            case "REFERRAL_BONUS": return "Referral Bonus";
            case "REVIEW_BONUS": return "Review Bonus";
            case "LOYALTY_BONUS": return "Loyalty Bonus";
            case "SPECIAL_PROMOTION": return "Special Promotion";
            case "RECYCLING_MILESTONE": return "Recycling Milestone";
            default: return "Reward";
        }
    }
}
