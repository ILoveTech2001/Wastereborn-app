package com.wastereborn.repository;

import com.wastereborn.model.PickupRequest;
import com.wastereborn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PickupRequestRepository extends JpaRepository<PickupRequest, Long> {
    
    List<PickupRequest> findByUser(User user);
    
    List<PickupRequest> findByStatus(PickupRequest.PickupStatus status);
    
    List<PickupRequest> findByUserAndStatus(User user, PickupRequest.PickupStatus status);
    
    @Query("SELECT pr FROM PickupRequest pr WHERE pr.pickupStreet = ?1 AND pr.status IN ('PENDING', 'SCHEDULED')")
    List<PickupRequest> findPendingPickupsByStreet(String street);
    
    @Query("SELECT pr FROM PickupRequest pr WHERE pr.scheduledPickupDate BETWEEN ?1 AND ?2")
    List<PickupRequest> findByScheduledPickupDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT pr FROM PickupRequest pr WHERE pr.user = ?1 ORDER BY pr.createdAt DESC")
    List<PickupRequest> findByUserOrderByCreatedAtDesc(User user);
    
    @Query("SELECT pr FROM PickupRequest pr WHERE pr.assignedTo = ?1 AND pr.status IN ('SCHEDULED', 'IN_PROGRESS')")
    List<PickupRequest> findActivePickupsByAssignee(User assignee);
    
    @Query("SELECT COUNT(pr) FROM PickupRequest pr WHERE pr.user = ?1 AND pr.status = 'COMPLETED'")
    Long countCompletedPickupsByUser(User user);
}
