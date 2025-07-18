package com.wastereborn.repository;

import com.wastereborn.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findByName(String name);
    
    List<Category> findByIsActiveTrue();
    
    @Query("SELECT c FROM Category c WHERE c.isActive = true ORDER BY c.name")
    List<Category> findActiveCategoriesOrderByName();
    
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = ?1")
    Optional<Category> findByIdWithProducts(Long id);
    
    boolean existsByName(String name);
}
