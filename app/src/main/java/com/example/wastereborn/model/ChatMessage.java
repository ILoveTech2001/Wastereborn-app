package com.example.wastereborn.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatMessage {
    private String message;
    private boolean isUser;
    private long timestamp;
    private String messageId;
    
    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
        this.timestamp = System.currentTimeMillis();
        this.messageId = generateMessageId();
    }
    
    public ChatMessage(String message, boolean isUser, long timestamp) {
        this.message = message;
        this.isUser = isUser;
        this.timestamp = timestamp;
        this.messageId = generateMessageId();
    }
    
    private String generateMessageId() {
        return "msg_" + timestamp + "_" + (isUser ? "user" : "bot");
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isUser() {
        return isUser;
    }
    
    public void setUser(boolean user) {
        isUser = user;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getMessageId() {
        return messageId;
    }
    
    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
