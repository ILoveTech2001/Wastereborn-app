package com.wastereborn.repository;

import com.wastereborn.model.Product;
import com.wastereborn.model.Review;
import com.wastereborn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByProduct(Product product);
    
    List<Review> findByUser(User user);
    
    List<Review> findByProductAndIsApprovedTrue(Product product);
    
    @Query("SELECT r FROM Review r WHERE r.product = ?1 AND r.isApproved = true ORDER BY r.datePosted DESC")
    List<Review> findApprovedReviewsByProductOrderByDateDesc(Product product);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product = ?1 AND r.isApproved = true")
    Double getAverageRatingForProduct(Product product);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product = ?1 AND r.isApproved = true")
    Long countApprovedReviewsForProduct(Product product);
    
    @Query("SELECT r FROM Review r WHERE r.datePosted BETWEEN ?1 AND ?2")
    List<Review> findReviewsByDateRange(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT r FROM Review r WHERE r.isApproved = false")
    List<Review> findPendingReviews();
    
    boolean existsByUserAndProduct(User user, Product product);
}
