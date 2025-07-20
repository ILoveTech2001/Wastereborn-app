package com.example.wastereborn.utils;

public class Constants {
    
    // Delivery and Shipping
    public static final double DELIVERY_FEE = 1000.0; // FCFA
    
    // Points System
    public static final double POINTS_TO_FCFA_RATIO = 10.0; // 10 FCFA = 1 point
    
    // Payment Methods
    public static final String PAYMENT_MTN = "MTN_MOBILE_MONEY";
    public static final String PAYMENT_ORANGE = "ORANGE_MONEY";
    public static final String PAYMENT_CASH = "CASH_ON_DELIVERY";
    public static final String PAYMENT_POINTS = "POINTS";
    
    // API Endpoints
    public static final String API_BASE_URL = "http://10.0.2.2:8080/api/";
    
    // Default Values
    public static final String DEFAULT_CITY = "Yaoundé";
    
    // Validation
    public static final String PHONE_REGEX = "^(6[5-9]\\d{7}|2[0-9]{8})$";
    
    private Constants() {
        // Prevent instantiation
    }
}
