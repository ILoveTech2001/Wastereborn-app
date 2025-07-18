package com.wastereborn.service;

import com.wastereborn.model.PickupRequest;
import com.wastereborn.model.User;
import com.wastereborn.repository.PickupRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PickupRequestService {

    @Autowired
    private PickupRequestRepository pickupRequestRepository;

    @Autowired
    private UserService userService;

    public List<PickupRequest> getAllPickupRequests() {
        return pickupRequestRepository.findAll();
    }

    public List<PickupRequest> getPickupRequestsByUser(User user) {
        return pickupRequestRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<PickupRequest> getPickupRequestsByStatus(PickupRequest.PickupStatus status) {
        return pickupRequestRepository.findByStatus(status);
    }

    public List<PickupRequest> getPendingPickupsByStreet(String street) {
        return pickupRequestRepository.findPendingPickupsByStreet(street);
    }

    public Optional<PickupRequest> getPickupRequestById(Long id) {
        return pickupRequestRepository.findById(id);
    }

    public PickupRequest createPickupRequest(PickupRequest pickupRequest, User user) {
        pickupRequest.setUser(user);
        pickupRequest.setStatus(PickupRequest.PickupStatus.PENDING);
        return pickupRequestRepository.save(pickupRequest);
    }

    public PickupRequest updatePickupRequest(Long id, PickupRequest updatedPickupRequest) {
        PickupRequest pickupRequest = pickupRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pickup request not found"));

        pickupRequest.setPickupAddress(updatedPickupRequest.getPickupAddress());
        pickupRequest.setPickupStreet(updatedPickupRequest.getPickupStreet());
        pickupRequest.setPickupCity(updatedPickupRequest.getPickupCity());
        pickupRequest.setWasteType(updatedPickupRequest.getWasteType());
        pickupRequest.setEstimatedWeight(updatedPickupRequest.getEstimatedWeight());
        pickupRequest.setSpecialInstructions(updatedPickupRequest.getSpecialInstructions());
        pickupRequest.setPreferredPickupDate(updatedPickupRequest.getPreferredPickupDate());

        return pickupRequestRepository.save(pickupRequest);
    }

    public PickupRequest updatePickupStatus(Long id, PickupRequest.PickupStatus status, User assignedTo) {
        PickupRequest pickupRequest = pickupRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pickup request not found"));

        pickupRequest.setStatus(status);
        
        if (assignedTo != null) {
            pickupRequest.setAssignedTo(assignedTo);
        }

        if (status == PickupRequest.PickupStatus.SCHEDULED) {
            // Set scheduled pickup date (could be based on street schedule)
            pickupRequest.setScheduledPickupDate(LocalDateTime.now().plusDays(1));
        } else if (status == PickupRequest.PickupStatus.COMPLETED) {
            pickupRequest.setActualPickupDate(LocalDateTime.now());
            
            // Award points to user (5 points per pickup)
            pickupRequest.setPointsAwarded(5);
            userService.addPoints(pickupRequest.getUser().getId(), 5);
            
            // Update user stats
            userService.updateUserStats(pickupRequest.getUser().getId(), false, true);
        }

        return pickupRequestRepository.save(pickupRequest);
    }

    public void deletePickupRequest(Long id) {
        pickupRequestRepository.deleteById(id);
    }

    public List<PickupRequest> getPickupRequestsByDateRange(LocalDateTime start, LocalDateTime end) {
        return pickupRequestRepository.findByScheduledPickupDateBetween(start, end);
    }

    public List<PickupRequest> getActivePickupsByAssignee(User assignee) {
        return pickupRequestRepository.findActivePickupsByAssignee(assignee);
    }

    public Long getCompletedPickupsCountByUser(User user) {
        return pickupRequestRepository.countCompletedPickupsByUser(user);
    }

    public PickupRequest schedulePickup(Long id, LocalDateTime scheduledDate, User assignedTo) {
        PickupRequest pickupRequest = pickupRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pickup request not found"));

        pickupRequest.setStatus(PickupRequest.PickupStatus.SCHEDULED);
        pickupRequest.setScheduledPickupDate(scheduledDate);
        pickupRequest.setAssignedTo(assignedTo);

        return pickupRequestRepository.save(pickupRequest);
    }

    public PickupRequest addPickupNotes(Long id, String notes) {
        PickupRequest pickupRequest = pickupRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pickup request not found"));

        pickupRequest.setPickupNotes(notes);
        return pickupRequestRepository.save(pickupRequest);
    }
}
