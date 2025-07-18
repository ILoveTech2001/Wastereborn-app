package com.wastereborn.service;

import com.wastereborn.model.Product;
import com.wastereborn.model.Review;
import com.wastereborn.model.User;
import com.wastereborn.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductService productService;

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> getReviewsByProduct(Product product) {
        return reviewRepository.findApprovedReviewsByProductOrderByDateDesc(product);
    }

    public List<Review> getReviewsByUser(User user) {
        return reviewRepository.findByUser(user);
    }

    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    public Review createReview(Review review) {
        // Check if user already reviewed this product
        if (reviewRepository.existsByUserAndProduct(review.getUser(), review.getProduct())) {
            throw new RuntimeException("User has already reviewed this product");
        }

        Review savedReview = reviewRepository.save(review);
        
        // Update product average rating
        updateProductRating(review.getProduct());
        
        return savedReview;
    }

    public Review updateReview(Long id, Review updatedReview) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setRating(updatedReview.getRating());
        review.setComment(updatedReview.getComment());

        Review savedReview = reviewRepository.save(review);
        
        // Update product average rating
        updateProductRating(review.getProduct());
        
        return savedReview;
    }

    public Review approveReview(Long id, boolean approved) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setIsApproved(approved);
        Review savedReview = reviewRepository.save(review);
        
        // Update product average rating
        updateProductRating(review.getProduct());
        
        return savedReview;
    }

    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        
        Product product = review.getProduct();
        reviewRepository.deleteById(id);
        
        // Update product average rating
        updateProductRating(product);
    }

    public Double getAverageRatingForProduct(Product product) {
        return reviewRepository.getAverageRatingForProduct(product);
    }

    public Long getReviewCountForProduct(Product product) {
        return reviewRepository.countApprovedReviewsForProduct(product);
    }

    public List<Review> getPendingReviews() {
        return reviewRepository.findPendingReviews();
    }

    private void updateProductRating(Product product) {
        Double averageRating = reviewRepository.getAverageRatingForProduct(product);
        Long reviewCount = reviewRepository.countApprovedReviewsForProduct(product);
        
        product.setAverageRating(averageRating != null ? averageRating : 0.0);
        product.setReviewCount(reviewCount != null ? reviewCount.intValue() : 0);
        
        productService.updateProduct(product.getId(), product);
    }
}
