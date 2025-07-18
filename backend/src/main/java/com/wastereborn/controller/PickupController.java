package com.wastereborn.controller;

import com.wastereborn.model.PickupRequest;
import com.wastereborn.model.User;
import com.wastereborn.service.PickupRequestService;
import com.wastereborn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/pickups")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PickupController {

    @Autowired
    private PickupRequestService pickupRequestService;

    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public ResponseEntity<List<PickupRequest>> getUserPickups(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<PickupRequest> pickups = pickupRequestService.getPickupRequestsByUser(user);
        return ResponseEntity.ok(pickups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PickupRequest> getPickupById(@PathVariable Long id, Authentication authentication) {
        PickupRequest pickup = pickupRequestService.getPickupRequestById(id)
            .orElse(null);
        
        if (pickup == null) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userService.findByEmail(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user owns this pickup or is admin
        if (!pickup.getUser().getId().equals(user.getId()) && !user.getRole().equals(User.Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(pickup);
    }

    @PostMapping
    public ResponseEntity<PickupRequest> createPickupRequest(@RequestBody PickupRequestDto pickupRequestDto, 
                                                           Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        PickupRequest pickupRequest = new PickupRequest();
        pickupRequest.setPickupAddress(pickupRequestDto.getPickupAddress());
        pickupRequest.setPickupStreet(pickupRequestDto.getPickupStreet());
        pickupRequest.setPickupCity(pickupRequestDto.getPickupCity());
        pickupRequest.setWasteType(pickupRequestDto.getWasteType());
        pickupRequest.setEstimatedWeight(pickupRequestDto.getEstimatedWeight());
        pickupRequest.setSpecialInstructions(pickupRequestDto.getSpecialInstructions());
        
        // Set preferred pickup date (could be parsed from slot info)
        pickupRequest.setPreferredPickupDate(LocalDateTime.now().plusDays(1));
        
        PickupRequest createdPickup = pickupRequestService.createPickupRequest(pickupRequest, user);
        return ResponseEntity.ok(createdPickup);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PickupRequest> updatePickupRequest(@PathVariable Long id, 
                                                           @RequestBody PickupRequestDto pickupRequestDto,
                                                           Authentication authentication) {
        PickupRequest existingPickup = pickupRequestService.getPickupRequestById(id)
            .orElse(null);
        
        if (existingPickup == null) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userService.findByEmail(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user owns this pickup
        if (!existingPickup.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Only allow updates if pickup is still pending
        if (existingPickup.getStatus() != PickupRequest.PickupStatus.PENDING) {
            return ResponseEntity.badRequest().build();
        }
        
        PickupRequest pickupRequest = new PickupRequest();
        pickupRequest.setPickupAddress(pickupRequestDto.getPickupAddress());
        pickupRequest.setPickupStreet(pickupRequestDto.getPickupStreet());
        pickupRequest.setPickupCity(pickupRequestDto.getPickupCity());
        pickupRequest.setWasteType(pickupRequestDto.getWasteType());
        pickupRequest.setEstimatedWeight(pickupRequestDto.getEstimatedWeight());
        pickupRequest.setSpecialInstructions(pickupRequestDto.getSpecialInstructions());
        
        PickupRequest updatedPickup = pickupRequestService.updatePickupRequest(id, pickupRequest);
        return ResponseEntity.ok(updatedPickup);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePickupRequest(@PathVariable Long id, Authentication authentication) {
        PickupRequest existingPickup = pickupRequestService.getPickupRequestById(id)
            .orElse(null);
        
        if (existingPickup == null) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userService.findByEmail(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user owns this pickup or is admin
        if (!existingPickup.getUser().getId().equals(user.getId()) && !user.getRole().equals(User.Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Only allow deletion if pickup is still pending
        if (existingPickup.getStatus() != PickupRequest.PickupStatus.PENDING) {
            return ResponseEntity.badRequest().build();
        }
        
        pickupRequestService.deletePickupRequest(id);
        return ResponseEntity.ok().build();
    }

    // Admin endpoints
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PickupRequest>> getAllPickups() {
        List<PickupRequest> pickups = pickupRequestService.getAllPickupRequests();
        return ResponseEntity.ok(pickups);
    }

    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PickupRequest>> getPickupsByStatus(@PathVariable String status) {
        try {
            PickupRequest.PickupStatus pickupStatus = PickupRequest.PickupStatus.valueOf(status.toUpperCase());
            List<PickupRequest> pickups = pickupRequestService.getPickupRequestsByStatus(pickupStatus);
            return ResponseEntity.ok(pickups);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PickupRequest> updatePickupStatus(@PathVariable Long id, 
                                                          @RequestParam String status,
                                                          @RequestParam(required = false) Long assignedToId) {
        try {
            PickupRequest.PickupStatus pickupStatus = PickupRequest.PickupStatus.valueOf(status.toUpperCase());
            
            User assignedTo = null;
            if (assignedToId != null) {
                assignedTo = userService.findById(assignedToId)
                    .orElseThrow(() -> new RuntimeException("Assigned user not found"));
            }
            
            PickupRequest updatedPickup = pickupRequestService.updatePickupStatus(id, pickupStatus, assignedTo);
            return ResponseEntity.ok(updatedPickup);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/admin/street/{street}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PickupRequest>> getPendingPickupsByStreet(@PathVariable String street) {
        List<PickupRequest> pickups = pickupRequestService.getPendingPickupsByStreet(street);
        return ResponseEntity.ok(pickups);
    }

    // DTO class for pickup request data
    public static class PickupRequestDto {
        private String pickupAddress;
        private String pickupStreet;
        private String pickupCity;
        private String wasteType;
        private Double estimatedWeight;
        private String specialInstructions;

        // Getters and setters
        public String getPickupAddress() { return pickupAddress; }
        public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

        public String getPickupStreet() { return pickupStreet; }
        public void setPickupStreet(String pickupStreet) { this.pickupStreet = pickupStreet; }

        public String getPickupCity() { return pickupCity; }
        public void setPickupCity(String pickupCity) { this.pickupCity = pickupCity; }

        public String getWasteType() { return wasteType; }
        public void setWasteType(String wasteType) { this.wasteType = wasteType; }

        public Double getEstimatedWeight() { return estimatedWeight; }
        public void setEstimatedWeight(Double estimatedWeight) { this.estimatedWeight = estimatedWeight; }

        public String getSpecialInstructions() { return specialInstructions; }
        public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }
    }
}
