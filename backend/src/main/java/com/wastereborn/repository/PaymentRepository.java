package com.wastereborn.repository;

import com.wastereborn.model.Order;
import com.wastereborn.model.Payment;
import com.wastereborn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByUser(User user);
    
    List<Payment> findByOrder(Order order);
    
    Optional<Payment> findByPaymentReference(String paymentReference);
    
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.user = ?1 ORDER BY p.createdAt DESC")
    List<Payment> findByUserOrderByCreatedAtDesc(User user);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.paymentDate BETWEEN ?1 AND ?2")
    Double getTotalRevenueByDateRange(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN ?1 AND ?2")
    List<Payment> findPaymentsByDateRange(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'COMPLETED' AND p.paymentDate BETWEEN ?1 AND ?2")
    Long countCompletedPaymentsByDateRange(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.createdAt < ?1")
    List<Payment> findPendingPaymentsOlderThan(LocalDateTime cutoffTime);
}
