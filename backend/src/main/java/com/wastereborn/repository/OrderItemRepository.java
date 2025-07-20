package com.wastereborn.repository;

import com.wastereborn.model.Order;
import com.wastereborn.model.OrderItem;
import com.wastereborn.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrder(Order order);
    
    List<OrderItem> findByProduct(Product product);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.user.id = ?1")
    List<OrderItem> findByUserId(Long userId);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.createdAt BETWEEN ?1 AND ?2")
    List<OrderItem> findByOrderDateRange(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT SUM(oi.quantity * oi.unitPrice) FROM OrderItem oi WHERE oi.order.id = ?1")
    Double getTotalAmountByOrder(Long orderId);
    
    @Query("SELECT oi.product, SUM(oi.quantity) FROM OrderItem oi GROUP BY oi.product ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findMostOrderedProducts();
    
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.product.id = ?1")
    Long countByProductId(Long productId);
}
