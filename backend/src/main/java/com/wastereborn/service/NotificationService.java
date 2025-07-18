package com.wastereborn.service;

import com.wastereborn.model.Notification;
import com.wastereborn.model.User;
import com.wastereborn.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getNotificationsByUser(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Notification> getUnreadNotificationsByUser(User user) {
        return notificationRepository.findUnreadByUserOrderByPriorityAndDate(user);
    }

    public Long getUnreadCountByUser(User user) {
        return notificationRepository.countUnreadByUser(user);
    }

    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public Notification sendNotification(User user, String title, String message, Notification.NotificationType type) {
        Notification notification = new Notification(user, title, message, type);
        notification.markAsSent();
        return notificationRepository.save(notification);
    }

    public Notification sendNotificationWithReference(User user, String title, String message, 
                                                    Notification.NotificationType type, 
                                                    String referenceId, String referenceType) {
        Notification notification = new Notification(user, title, message, type);
        notification.setReferenceId(referenceId);
        notification.setReferenceType(referenceType);
        notification.markAsSent();
        return notificationRepository.save(notification);
    }

    public Notification markAsRead(Long notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.markAsRead();
            return notificationRepository.save(notification);
        }
        throw new RuntimeException("Notification not found");
    }

    public void markAllAsRead(User user) {
        List<Notification> unreadNotifications = notificationRepository.findByUserAndIsReadFalse(user);
        for (Notification notification : unreadNotifications) {
            notification.markAsRead();
        }
        notificationRepository.saveAll(unreadNotifications);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    public void deleteExpiredNotifications() {
        List<Notification> expiredNotifications = notificationRepository.findExpiredNotifications(LocalDateTime.now());
        notificationRepository.deleteAll(expiredNotifications);
    }

    // Admin methods for sending notifications to users
    public void sendOrderNotification(User user, String orderNumber, String status) {
        String title = "Order Update";
        String message = String.format("Your order %s has been %s", orderNumber, status.toLowerCase());
        sendNotificationWithReference(user, title, message, Notification.NotificationType.ORDER, orderNumber, "ORDER");
    }

    public void sendPickupNotification(User user, String pickupId, String status) {
        String title = "Pickup Update";
        String message = String.format("Your pickup request has been %s", status.toLowerCase());
        sendNotificationWithReference(user, title, message, Notification.NotificationType.PICKUP, pickupId, "PICKUP");
    }

    public void sendSystemNotification(User user, String title, String message) {
        sendNotification(user, title, message, Notification.NotificationType.SYSTEM);
    }

    public void sendPromotionNotification(User user, String title, String message) {
        sendNotification(user, title, message, Notification.NotificationType.PROMOTION);
    }
}
