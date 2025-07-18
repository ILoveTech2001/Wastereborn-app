package com.wastereborn.repository;

import com.wastereborn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    List<User> findByRole(User.Role role);
    
    @Query("SELECT u FROM User u WHERE u.role = 'USER' ORDER BY u.pointsBalance DESC")
    List<User> findTopUsersByPoints();
    
    @Query("SELECT u FROM User u WHERE u.street = ?1")
    List<User> findByStreet(String street);
    
    @Query("SELECT u FROM User u WHERE u.city = ?1")
    List<User> findByCity(String city);
}
