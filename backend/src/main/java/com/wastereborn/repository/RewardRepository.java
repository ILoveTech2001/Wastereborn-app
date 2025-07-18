package com.wastereborn.repository;

import com.wastereborn.model.Reward;
import com.wastereborn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
    
    List<Reward> findByUser(User user);
    
    List<Reward> findByUserAndIsRedeemedFalse(User user);
    
    List<Reward> findByType(Reward.RewardType type);
    
    @Query("SELECT r FROM Reward r WHERE r.user = ?1 ORDER BY r.earnedDate DESC")
    List<Reward> findByUserOrderByEarnedDateDesc(User user);
    
    @Query("SELECT SUM(r.points) FROM Reward r WHERE r.user = ?1 AND r.isRedeemed = false")
    Integer getTotalAvailablePointsByUser(User user);
    
    @Query("SELECT r FROM Reward r WHERE r.expiresAt < ?1 AND r.isRedeemed = false")
    List<Reward> findExpiredUnredeemedRewards(LocalDateTime now);
    
    @Query("SELECT r FROM Reward r WHERE r.referenceId = ?1 AND r.referenceType = ?2")
    List<Reward> findByReference(String referenceId, String referenceType);
    
    @Query("SELECT r.type, SUM(r.points) FROM Reward r WHERE r.earnedDate BETWEEN ?1 AND ?2 GROUP BY r.type")
    List<Object[]> getPointsEarnedByTypeInDateRange(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(r) FROM Reward r WHERE r.earnedDate BETWEEN ?1 AND ?2")
    Long countRewardsEarnedInDateRange(LocalDateTime start, LocalDateTime end);
}
