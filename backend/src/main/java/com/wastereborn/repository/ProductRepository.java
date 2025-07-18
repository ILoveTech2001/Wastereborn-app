package com.wastereborn.repository;

import com.wastereborn.model.Category;
import com.wastereborn.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByCategory(Category category);

    List<Product> findByIsAvailable(Boolean isAvailable);

    List<Product> findByIsPointsRedeemable(Boolean isPointsRedeemable);

    @Query("SELECT p FROM Product p WHERE p.isAvailable = true ORDER BY p.createdAt DESC")
    List<Product> findAvailableProducts();

    @Query("SELECT p FROM Product p WHERE p.isPointsRedeemable = true AND p.isAvailable = true")
    List<Product> findPointsRedeemableProducts();

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Product> searchProducts(String keyword);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.isAvailable = true")
    List<Category> findDistinctCategories();

    @Query("SELECT p FROM Product p WHERE p.category = ?1 AND p.isAvailable = true")
    List<Product> findByCategoryAndAvailable(Category category);

    // Additional methods for category name search
    @Query("SELECT p FROM Product p WHERE p.category.name = ?1")
    List<Product> findByCategoryName(String categoryName);

    @Query("SELECT p FROM Product p WHERE p.category.name = ?1 AND p.isAvailable = true")
    List<Product> findByCategoryNameAndAvailable(String categoryName);
}
