package com.wastereborn.repository;

import com.wastereborn.model.Notification;
import com.wastereborn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUser(User user);
    
    List<Notification> findByUserAndIsReadFalse(User user);
    
    @Query("SELECT n FROM Notification n WHERE n.user = ?1 ORDER BY n.createdAt DESC")
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    
    @Query("SELECT n FROM Notification n WHERE n.user = ?1 AND n.isRead = false ORDER BY n.priority DESC, n.createdAt DESC")
    List<Notification> findUnreadByUserOrderByPriorityAndDate(User user);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = ?1 AND n.isRead = false")
    Long countUnreadByUser(User user);
    
    @Query("SELECT n FROM Notification n WHERE n.isSent = false AND (n.expiresAt IS NULL OR n.expiresAt > ?1)")
    List<Notification> findUnsentAndNotExpired(LocalDateTime now);
    
    @Query("SELECT n FROM Notification n WHERE n.expiresAt < ?1")
    List<Notification> findExpiredNotifications(LocalDateTime now);
    
    List<Notification> findByType(Notification.NotificationType type);
    
    @Query("SELECT n FROM Notification n WHERE n.referenceId = ?1 AND n.referenceType = ?2")
    List<Notification> findByReference(String referenceId, String referenceType);
}
