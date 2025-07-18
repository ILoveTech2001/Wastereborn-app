package com.example.wastereborn.model;

public class Notification {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private String type;
    private Boolean isRead;
    private Boolean isSent;
    private String referenceId;
    private String referenceType;
    private String actionUrl;
    private Integer priority;
    private String expiresAt;
    private String sentAt;
    private String readAt;
    private String createdAt;

    public Notification() {}

    public Notification(String title, String message, String type) {
        this.title = title;
        this.message = message;
        this.type = type;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public Boolean getIsSent() { return isSent; }
    public void setIsSent(Boolean isSent) { this.isSent = isSent; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }

    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }

    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }

    public String getReadAt() { return readAt; }
    public void setReadAt(String readAt) { this.readAt = readAt; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    // Utility methods
    public boolean isUnread() {
        return isRead == null || !isRead;
    }

    public String getTypeIcon() {
        switch (type != null ? type.toUpperCase() : "INFO") {
            case "SUCCESS": return "✅";
            case "WARNING": return "⚠️";
            case "ERROR": return "❌";
            case "ORDER": return "📦";
            case "PICKUP": return "🚛";
            case "PROMOTION": return "🎉";
            case "SYSTEM": return "⚙️";
            default: return "ℹ️";
        }
    }

    public int getTypeColor() {
        switch (type != null ? type.toUpperCase() : "INFO") {
            case "SUCCESS": return 0xFF4CAF50;
            case "WARNING": return 0xFFFF9800;
            case "ERROR": return 0xFFF44336;
            case "ORDER": return 0xFF2196F3;
            case "PICKUP": return 0xFF9C27B0;
            case "PROMOTION": return 0xFFE91E63;
            case "SYSTEM": return 0xFF607D8B;
            default: return 0xFF2196F3;
        }
    }
}
