package com.wastereborn.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Entity
@Table(name = "rewards")
public class Reward {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull
    @Positive
    @Column(name = "points")
    private Integer points;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private RewardType type;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "reference_id")
    private String referenceId;
    
    @Column(name = "reference_type")
    private String referenceType;
    
    @Column(name = "earned_date")
    private LocalDateTime earnedDate = LocalDateTime.now();
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "is_redeemed")
    private Boolean isRedeemed = false;
    
    @Column(name = "redeemed_at")
    private LocalDateTime redeemedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Constructors
    public Reward() {}
    
    public Reward(User user, Integer points, RewardType type, String description) {
        this.user = user;
        this.points = points;
        this.type = type;
        this.description = description;
    }
    
    public Reward(User user, Integer points, RewardType type, String description, String referenceId, String referenceType) {
        this.user = user;
        this.points = points;
        this.type = type;
        this.description = description;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    
    public RewardType getType() { return type; }
    public void setType(RewardType type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    
    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }
    
    public LocalDateTime getEarnedDate() { return earnedDate; }
    public void setEarnedDate(LocalDateTime earnedDate) { this.earnedDate = earnedDate; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public Boolean getIsRedeemed() { return isRedeemed; }
    public void setIsRedeemed(Boolean isRedeemed) { this.isRedeemed = isRedeemed; }
    
    public LocalDateTime getRedeemedAt() { return redeemedAt; }
    public void setRedeemedAt(LocalDateTime redeemedAt) { this.redeemedAt = redeemedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isValid() {
        return !isRedeemed && !isExpired();
    }
    
    public void redeem() {
        this.isRedeemed = true;
        this.redeemedAt = LocalDateTime.now();
    }
    
    public enum RewardType {
        SIGNUP_BONUS,
        PICKUP_COMPLETION,
        ORDER_PURCHASE,
        REFERRAL_BONUS,
        REVIEW_BONUS,
        LOYALTY_BONUS,
        SPECIAL_PROMOTION,
        RECYCLING_MILESTONE
    }
}
