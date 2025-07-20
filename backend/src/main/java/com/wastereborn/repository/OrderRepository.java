package com.wastereborn.repository;

import com.wastereborn.model.Order;
import com.wastereborn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByUser(User user);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    List<Order> findByUserAndStatus(User user, Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.user = ?1 ORDER BY o.createdAt DESC")
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN ?1 AND ?2")
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.paymentStatus = 'PAID'")
    Double getTotalRevenue();
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.paymentStatus = 'PAID' AND o.createdAt BETWEEN ?1 AND ?2")
    Double getRevenueByDateRange(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user = ?1 AND o.status = 'DELIVERED'")
    Long countDeliveredOrdersByUser(User user);
    
    @Query("SELECT o FROM Order o WHERE o.status IN ('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED') ORDER BY o.createdAt DESC")
    List<Order> findActiveOrders();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN ?1 AND ?2")
    Long countOrdersByDateRange(LocalDateTime start, LocalDateTime end);
}
