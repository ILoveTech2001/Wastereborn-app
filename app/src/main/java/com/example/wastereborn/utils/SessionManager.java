package com.example.wastereborn.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.wastereborn.model.JwtResponse;

public class SessionManager {
    
    private static final String PREF_NAME = "WasteRebornSession";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ROLE = "role";
    private static final String KEY_POINTS = "points";
    private static final String KEY_TOTAL_ORDERS = "total_orders";
    private static final String KEY_TOTAL_PICKUPS = "total_pickups";
    private static final String KEY_RECYCLING_PERCENTAGE = "recycling_percentage";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_IS_PREMIUM = "is_premium";
    private static final String KEY_PROFILE_IMAGE = "profile_image";
    private static final String KEY_REGISTRATION_DATE = "registration_date";
    
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }
    
    public void saveUserSession(JwtResponse user) {
        editor.putString(KEY_TOKEN, user.getToken());
        editor.putLong(KEY_USER_ID, user.getId());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_FIRST_NAME, user.getFirstName());
        editor.putString(KEY_LAST_NAME, user.getLastName());
        editor.putString(KEY_PHONE, user.getPhoneNumber());
        editor.putString(KEY_ROLE, user.getRole());
        editor.putInt(KEY_POINTS, user.getPointsBalance() != null ? user.getPointsBalance() : 0);
        editor.putInt(KEY_TOTAL_ORDERS, user.getTotalOrders() != null ? user.getTotalOrders() : 0);
        editor.putInt(KEY_TOTAL_PICKUPS, user.getTotalPickups() != null ? user.getTotalPickups() : 0);
        editor.putFloat(KEY_RECYCLING_PERCENTAGE, user.getRecyclingPercentage() != null ? user.getRecyclingPercentage().floatValue() : 0.0f);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }
    
    public JwtResponse getUserSession() {
        if (!isLoggedIn()) {
            return null;
        }
        
        JwtResponse user = new JwtResponse();
        user.setToken(pref.getString(KEY_TOKEN, null));
        user.setId(pref.getLong(KEY_USER_ID, 0));
        user.setEmail(pref.getString(KEY_EMAIL, null));
        user.setFirstName(pref.getString(KEY_FIRST_NAME, null));
        user.setLastName(pref.getString(KEY_LAST_NAME, null));
        user.setPhoneNumber(pref.getString(KEY_PHONE, null));
        user.setRole(pref.getString(KEY_ROLE, null));
        user.setPointsBalance(pref.getInt(KEY_POINTS, 0));
        user.setTotalOrders(pref.getInt(KEY_TOTAL_ORDERS, 0));
        user.setTotalPickups(pref.getInt(KEY_TOTAL_PICKUPS, 0));
        user.setRecyclingPercentage((double) pref.getFloat(KEY_RECYCLING_PERCENTAGE, 0.0f));
        
        return user;
    }
    
    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }
    
    public String getAuthHeader() {
        String token = getToken();
        return token != null ? "Bearer " + token : null;
    }
    
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    public boolean isAdmin() {
        return "ADMIN".equals(pref.getString(KEY_ROLE, "USER"));
    }
    
    public void updateUserStats(int points, int orders, int pickups, double recyclingPercentage) {
        editor.putInt(KEY_POINTS, points);
        editor.putInt(KEY_TOTAL_ORDERS, orders);
        editor.putInt(KEY_TOTAL_PICKUPS, pickups);
        editor.putFloat(KEY_RECYCLING_PERCENTAGE, (float) recyclingPercentage);
        editor.apply();
    }
    
    public void logout() {
        editor.clear();
        editor.apply();
    }
    
    public String getUserName() {
        String firstName = pref.getString(KEY_FIRST_NAME, "");
        String lastName = pref.getString(KEY_LAST_NAME, "");
        return (firstName + " " + lastName).trim();
    }

    public String getUserEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public String getUserInitial() {
        String firstName = pref.getString(KEY_FIRST_NAME, "");
        return firstName.isEmpty() ? "U" : firstName.substring(0, 1).toUpperCase();
    }
    
    public int getPointsBalance() {
        return pref.getInt(KEY_POINTS, 0);
    }
    
    public int getTotalOrders() {
        return pref.getInt(KEY_TOTAL_ORDERS, 0);
    }
    
    public int getTotalPickups() {
        return pref.getInt(KEY_TOTAL_PICKUPS, 0);
    }
    
    public double getRecyclingPercentage() {
        return pref.getFloat(KEY_RECYCLING_PERCENTAGE, 0.0f);
    }

    public boolean isPremium() {
        return pref.getBoolean(KEY_IS_PREMIUM, false);
    }

    public void setPremium(boolean isPremium) {
        editor.putBoolean(KEY_IS_PREMIUM, isPremium);
        editor.apply();
    }

    public String getProfileImage() {
        return pref.getString(KEY_PROFILE_IMAGE, null);
    }

    public void setProfileImage(String imageUrl) {
        editor.putString(KEY_PROFILE_IMAGE, imageUrl);
        editor.apply();
    }

    public String getRegistrationDate() {
        return pref.getString(KEY_REGISTRATION_DATE, null);
    }

    public void setRegistrationDate(String registrationDate) {
        editor.putString(KEY_REGISTRATION_DATE, registrationDate);
        editor.apply();
    }
}
