package com.wastereborn.repository;

import com.wastereborn.model.Subscription;
import com.wastereborn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    List<Subscription> findByUser(User user);
    
    Optional<Subscription> findByUserAndIsActiveTrue(User user);
    
    List<Subscription> findByPlanType(String planType);
    
    @Query("SELECT s FROM Subscription s WHERE s.isActive = true AND s.endDate < ?1")
    List<Subscription> findExpiredActiveSubscriptions(LocalDateTime now);
    
    @Query("SELECT s FROM Subscription s WHERE s.autoRenew = true AND s.endDate BETWEEN ?1 AND ?2")
    List<Subscription> findSubscriptionsForRenewal(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.isActive = true")
    Long countActiveSubscriptions();
    
    @Query("SELECT s.planType, COUNT(s) FROM Subscription s WHERE s.isActive = true GROUP BY s.planType")
    List<Object[]> getActiveSubscriptionsByPlanType();
    
    @Query("SELECT s FROM Subscription s WHERE s.user = ?1 ORDER BY s.createdAt DESC")
    List<Subscription> findByUserOrderByCreatedAtDesc(User user);
}
