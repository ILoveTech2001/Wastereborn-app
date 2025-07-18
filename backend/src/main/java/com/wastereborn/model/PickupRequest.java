package com.wastereborn.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "pickup_requests")
public class PickupRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "pickup_address", nullable = false)
    private String pickupAddress;
    
    @Column(name = "pickup_street")
    private String pickupStreet;
    
    @Column(name = "pickup_city")
    private String pickupCity;
    
    @Column(name = "waste_type")
    private String wasteType;
    
    @Column(name = "estimated_weight")
    private Double estimatedWeight;
    
    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;
    
    @Column(name = "preferred_pickup_date")
    private LocalDateTime preferredPickupDate;
    
    @Column(name = "scheduled_pickup_date")
    private LocalDateTime scheduledPickupDate;
    
    @Column(name = "actual_pickup_date")
    private LocalDateTime actualPickupDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PickupStatus status = PickupStatus.PENDING;
    
    @Column(name = "points_awarded")
    private Integer pointsAwarded = 0;
    
    @Column(name = "pickup_notes", columnDefinition = "TEXT")
    private String pickupNotes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_driver_id")
    private Driver assignedDriver;

    @Column(name = "confirmed_by_user")
    private Boolean confirmedByUser = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public PickupRequest() {}
    
    public PickupRequest(User user, String pickupAddress, String wasteType, LocalDateTime preferredPickupDate) {
        this.user = user;
        this.pickupAddress = pickupAddress;
        this.wasteType = wasteType;
        this.preferredPickupDate = preferredPickupDate;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
    
    public String getPickupStreet() { return pickupStreet; }
    public void setPickupStreet(String pickupStreet) { this.pickupStreet = pickupStreet; }
    
    public String getPickupCity() { return pickupCity; }
    public void setPickupCity(String pickupCity) { this.pickupCity = pickupCity; }
    
    public String getWasteType() { return wasteType; }
    public void setWasteType(String wasteType) { this.wasteType = wasteType; }
    
    public Double getEstimatedWeight() { return estimatedWeight; }
    public void setEstimatedWeight(Double estimatedWeight) { this.estimatedWeight = estimatedWeight; }
    
    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }
    
    public LocalDateTime getPreferredPickupDate() { return preferredPickupDate; }
    public void setPreferredPickupDate(LocalDateTime preferredPickupDate) { this.preferredPickupDate = preferredPickupDate; }
    
    public LocalDateTime getScheduledPickupDate() { return scheduledPickupDate; }
    public void setScheduledPickupDate(LocalDateTime scheduledPickupDate) { this.scheduledPickupDate = scheduledPickupDate; }
    
    public LocalDateTime getActualPickupDate() { return actualPickupDate; }
    public void setActualPickupDate(LocalDateTime actualPickupDate) { this.actualPickupDate = actualPickupDate; }
    
    public PickupStatus getStatus() { return status; }
    public void setStatus(PickupStatus status) { this.status = status; }
    
    public Integer getPointsAwarded() { return pointsAwarded; }
    public void setPointsAwarded(Integer pointsAwarded) { this.pointsAwarded = pointsAwarded; }
    
    public String getPickupNotes() { return pickupNotes; }
    public void setPickupNotes(String pickupNotes) { this.pickupNotes = pickupNotes; }
    
    public User getAssignedTo() { return assignedTo; }
    public void setAssignedTo(User assignedTo) { this.assignedTo = assignedTo; }

    public Driver getAssignedDriver() { return assignedDriver; }
    public void setAssignedDriver(Driver assignedDriver) { this.assignedDriver = assignedDriver; }

    public Boolean getConfirmedByUser() { return confirmedByUser; }
    public void setConfirmedByUser(Boolean confirmedByUser) { this.confirmedByUser = confirmedByUser; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Business methods from class diagram
    public void confirmStatus() {
        this.confirmedByUser = true;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canBeAssignedToDriver() {
        return this.status == PickupStatus.SCHEDULED && this.assignedDriver == null;
    }

    public void assignToDriver(Driver driver) {
        if (canBeAssignedToDriver()) {
            this.assignedDriver = driver;
            this.status = PickupStatus.SCHEDULED;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public enum PickupStatus {
        PENDING, SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}
