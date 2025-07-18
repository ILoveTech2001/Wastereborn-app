package com.wastereborn.repository;

import com.wastereborn.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    
    Optional<Driver> findByEmail(String email);
    
    Optional<Driver> findByPhone(String phone);
    
    List<Driver> findByIsActiveTrue();
    
    List<Driver> findByStatus(Driver.DriverStatus status);
    
    @Query("SELECT d FROM Driver d WHERE d.isActive = true AND d.status = 'AVAILABLE'")
    List<Driver> findAvailableDrivers();
    
    @Query("SELECT d FROM Driver d WHERE d.isActive = true ORDER BY d.rating DESC")
    List<Driver> findActiveDriversOrderByRating();
    
    @Query("SELECT d FROM Driver d WHERE d.currentLocation = ?1 AND d.status = 'AVAILABLE'")
    List<Driver> findAvailableDriversByLocation(String location);
    
    @Query("SELECT d FROM Driver d WHERE d.lastActive < ?1")
    List<Driver> findInactiveDriversSince(LocalDateTime cutoffTime);
    
    @Query("SELECT COUNT(d) FROM Driver d WHERE d.isActive = true")
    Long countActiveDrivers();
    
    @Query("SELECT COUNT(d) FROM Driver d WHERE d.status = 'AVAILABLE'")
    Long countAvailableDrivers();
    
    boolean existsByEmail(String email);
    
    boolean existsByPhone(String phone);
}
