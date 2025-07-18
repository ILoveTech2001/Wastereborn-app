package com.wastereborn.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "drivers")
public class Driver {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 50)
    @Column(name = "name")
    private String name;
    
    @NotBlank
    @Email
    @Size(max = 100)
    @Column(name = "email", unique = true)
    private String email;
    
    @NotBlank
    @Size(max = 15)
    @Column(name = "phone", unique = true)
    private String phone;
    
    @Column(name = "license_number")
    private String licenseNumber;
    
    @Column(name = "vehicle_type")
    private String vehicleType;
    
    @Column(name = "vehicle_plate")
    private String vehiclePlate;
    
    @Column(name = "address")
    private String address;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DriverStatus status = DriverStatus.AVAILABLE;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "rating")
    private Double rating = 0.0;
    
    @Column(name = "total_pickups")
    private Integer totalPickups = 0;
    
    @Column(name = "completed_pickups")
    private Integer completedPickups = 0;
    
    @Column(name = "current_location")
    private String currentLocation;
    
    @Column(name = "hire_date")
    private LocalDateTime hireDate = LocalDateTime.now();
    
    @Column(name = "last_active")
    private LocalDateTime lastActive = LocalDateTime.now();
    
    @OneToMany(mappedBy = "assignedDriver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PickupRequest> assignedPickups;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public Driver() {}
    
    public Driver(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    
    public String getVehiclePlate() { return vehiclePlate; }
    public void setVehiclePlate(String vehiclePlate) { this.vehiclePlate = vehiclePlate; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public DriverStatus getStatus() { return status; }
    public void setStatus(DriverStatus status) { this.status = status; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    
    public Integer getTotalPickups() { return totalPickups; }
    public void setTotalPickups(Integer totalPickups) { this.totalPickups = totalPickups; }
    
    public Integer getCompletedPickups() { return completedPickups; }
    public void setCompletedPickups(Integer completedPickups) { this.completedPickups = completedPickups; }
    
    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
    
    public LocalDateTime getHireDate() { return hireDate; }
    public void setHireDate(LocalDateTime hireDate) { this.hireDate = hireDate; }
    
    public LocalDateTime getLastActive() { return lastActive; }
    public void setLastActive(LocalDateTime lastActive) { this.lastActive = lastActive; }
    
    public List<PickupRequest> getAssignedPickups() { return assignedPickups; }
    public void setAssignedPickups(List<PickupRequest> assignedPickups) { this.assignedPickups = assignedPickups; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateLastActive() {
        this.lastActive = LocalDateTime.now();
    }
    
    public double getCompletionRate() {
        if (totalPickups == 0) return 0.0;
        return (double) completedPickups / totalPickups * 100;
    }
    
    public enum DriverStatus {
        AVAILABLE, BUSY, ON_PICKUP, OFF_DUTY, INACTIVE
    }
}
