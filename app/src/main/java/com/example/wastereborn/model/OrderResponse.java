package com.example.wastereborn.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderResponse {
    
    @SerializedName("id")
    private Long id;
    
    @SerializedName("orderNumber")
    private String orderNumber;
    
    @SerializedName("totalAmount")
    private Double totalAmount;
    
    @SerializedName("deliveryFee")
    private Double deliveryFee;
    
    @SerializedName("deliveryAddress")
    private String deliveryAddress;
    
    @SerializedName("deliveryCity")
    private String deliveryCity;
    
    @SerializedName("deliveryPhone")
    private String deliveryPhone;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("orderStatus")
    private String orderStatus;
    
    @SerializedName("paymentMethod")
    private String paymentMethod;
    
    @SerializedName("paymentStatus")
    private String paymentStatus;
    
    @SerializedName("paymentReference")
    private String paymentReference;
    
    @SerializedName("estimatedDeliveryDate")
    private String estimatedDeliveryDate;
    
    @SerializedName("actualDeliveryDate")
    private String actualDeliveryDate;
    
    @SerializedName("orderNotes")
    private String orderNotes;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("updatedAt")
    private String updatedAt;
    
    @SerializedName("orderItems")
    private List<OrderItemResponse> orderItems;
    
    // Constructors
    public OrderResponse() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    
    public Double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(Double deliveryFee) { this.deliveryFee = deliveryFee; }
    
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    
    public String getDeliveryCity() { return deliveryCity; }
    public void setDeliveryCity(String deliveryCity) { this.deliveryCity = deliveryCity; }
    
    public String getDeliveryPhone() { return deliveryPhone; }
    public void setDeliveryPhone(String deliveryPhone) { this.deliveryPhone = deliveryPhone; }
    
    public String getStatus() { return status != null ? status : orderStatus; }
    public void setStatus(String status) { this.status = status; }
    
    public String getOrderStatus() { return orderStatus != null ? orderStatus : status; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
    
    public String getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
    public void setEstimatedDeliveryDate(String estimatedDeliveryDate) { this.estimatedDeliveryDate = estimatedDeliveryDate; }
    
    public String getActualDeliveryDate() { return actualDeliveryDate; }
    public void setActualDeliveryDate(String actualDeliveryDate) { this.actualDeliveryDate = actualDeliveryDate; }
    
    public String getOrderNotes() { return orderNotes; }
    public void setOrderNotes(String orderNotes) { this.orderNotes = orderNotes; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    
    public List<OrderItemResponse> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemResponse> orderItems) { this.orderItems = orderItems; }
    
    // Helper methods
    public String getFormattedAmount() {
        return String.format("%.0f FCFA", totalAmount != null ? totalAmount : 0.0);
    }
    
    public String getFormattedStatus() {
        String currentStatus = getStatus();
        if (currentStatus == null) return "Unknown";
        
        switch (currentStatus.toUpperCase()) {
            case "PENDING": return "Pending";
            case "CONFIRMED": return "Confirmed";
            case "PROCESSING": return "Processing";
            case "SHIPPED": return "Shipped";
            case "DELIVERED": return "Delivered";
            case "CANCELLED": return "Cancelled";
            default: return currentStatus;
        }
    }
    
    public String getItemCount() {
        if (orderItems == null || orderItems.isEmpty()) {
            return "0 items";
        }
        
        int totalItems = orderItems.stream()
                .mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0)
                .sum();
        
        return totalItems + (totalItems == 1 ? " item" : " items");
    }
    
    public String getFormattedDate() {
        if (createdAt == null) return "Unknown date";
        
        try {
            // Simple date formatting - you might want to use a proper date formatter
            if (createdAt.contains("T")) {
                String datePart = createdAt.split("T")[0];
                return datePart;
            }
            return createdAt;
        } catch (Exception e) {
            return "Unknown date";
        }
    }
    
    // Nested class for order items
    public static class OrderItemResponse {
        @SerializedName("id")
        private Long id;
        
        @SerializedName("quantity")
        private Integer quantity;
        
        @SerializedName("unitPrice")
        private Double unitPrice;
        
        @SerializedName("product")
        private ProductResponse product;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public Double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
        
        public ProductResponse getProduct() { return product; }
        public void setProduct(ProductResponse product) { this.product = product; }
    }
}
