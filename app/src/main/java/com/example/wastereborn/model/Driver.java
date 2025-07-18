package com.example.wastereborn.model;

public class Driver {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String licenseNumber;
    private String vehicleType;
    private String vehiclePlate;
    private String address;
    private String status;
    private Boolean isActive;
    private Double rating;
    private Integer totalPickups;
    private Integer completedPickups;
    private String currentLocation;
    private String hireDate;
    private String lastActive;
    private String createdAt;
    private String updatedAt;

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

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

    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }

    public String getLastActive() { return lastActive; }
    public void setLastActive(String lastActive) { this.lastActive = lastActive; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // Utility methods
    public double getCompletionRate() {
        if (totalPickups == null || totalPickups == 0) return 0.0;
        return (double) (completedPickups != null ? completedPickups : 0) / totalPickups * 100;
    }

    public String getStatusDisplayName() {
        switch (status != null ? status.toUpperCase() : "AVAILABLE") {
            case "AVAILABLE": return "Available";
            case "BUSY": return "Busy";
            case "ON_PICKUP": return "On Pickup";
            case "OFF_DUTY": return "Off Duty";
            case "INACTIVE": return "Inactive";
            default: return "Unknown";
        }
    }

    public int getStatusColor() {
        switch (status != null ? status.toUpperCase() : "AVAILABLE") {
            case "AVAILABLE": return 0xFF4CAF50;
            case "BUSY": return 0xFFFF9800;
            case "ON_PICKUP": return 0xFF2196F3;
            case "OFF_DUTY": return 0xFF9E9E9E;
            case "INACTIVE": return 0xFFF44336;
            default: return 0xFF9E9E9E;
        }
    }
}
