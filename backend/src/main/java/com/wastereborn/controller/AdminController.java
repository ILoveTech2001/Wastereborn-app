package com.wastereborn.controller;

import com.wastereborn.model.*;
import com.wastereborn.repository.*;
import com.wastereborn.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PickupRequestRepository pickupRequestRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private ProductService productService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // User statistics
        List<User> allUsers = userService.getAllUsers();
        List<User> regularUsers = userService.getUsersByRole(User.Role.USER);
        List<User> adminUsers = userService.getUsersByRole(User.Role.ADMIN);
        
        stats.put("totalUsers", allUsers.size());
        stats.put("regularUsers", regularUsers.size());
        stats.put("adminUsers", adminUsers.size());
        
        // Order statistics
        List<Order> allOrders = orderRepository.findAll();
        List<Order> pendingOrders = orderRepository.findByStatus(Order.OrderStatus.PENDING);
        List<Order> completedOrders = orderRepository.findByStatus(Order.OrderStatus.DELIVERED);
        
        stats.put("totalOrders", allOrders.size());
        stats.put("pendingOrders", pendingOrders.size());
        stats.put("completedOrders", completedOrders.size());
        
        // Pickup statistics
        List<PickupRequest> allPickups = pickupRequestRepository.findAll();
        List<PickupRequest> pendingPickups = pickupRequestRepository.findByStatus(PickupRequest.PickupStatus.PENDING);
        List<PickupRequest> completedPickups = pickupRequestRepository.findByStatus(PickupRequest.PickupStatus.COMPLETED);
        
        stats.put("totalPickups", allPickups.size());
        stats.put("pendingPickups", pendingPickups.size());
        stats.put("completedPickups", completedPickups.size());
        
        // Product statistics
        List<Product> allProducts = productRepository.findAll();
        List<Product> availableProducts = productRepository.findByIsAvailable(true);
        List<Product> pointsProducts = productRepository.findByIsPointsRedeemable(true);
        
        stats.put("totalProducts", allProducts.size());
        stats.put("availableProducts", availableProducts.size());
        stats.put("pointsProducts", pointsProducts.size());
        
        // Revenue statistics
        Double totalRevenue = orderRepository.getTotalRevenue();
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        
        // Recent activity
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
        Double weeklyRevenue = orderRepository.getRevenueByDateRange(weekAgo, LocalDateTime.now());
        stats.put("weeklyRevenue", weeklyRevenue != null ? weeklyRevenue : 0.0);
        
        List<Order> recentOrders = orderRepository.findByCreatedAtBetween(weekAgo, LocalDateTime.now());
        stats.put("weeklyOrders", recentOrders.size());
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/all")
    public ResponseEntity<List<User>> getAllUsersDetailed() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest createUserRequest) {
        try {
            User user = new User(
                createUserRequest.getFirstName(),
                createUserRequest.getLastName(),
                createUserRequest.getEmail(),
                createUserRequest.getPhoneNumber(),
                createUserRequest.getPassword()
            );

            if (createUserRequest.getRole() != null) {
                user.setRole(User.Role.valueOf(createUserRequest.getRole().toUpperCase()));
            }

            User savedUser = userService.createUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable Long id, @RequestParam String role) {
        try {
            User.Role userRole = User.Role.valueOf(role.toUpperCase());
            User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            user.setRole(userRole);
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<User> updateUserStatus(@PathVariable Long id, @RequestParam boolean enabled) {
        User user = userService.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setEnabled(enabled);
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders(Authentication authentication) {
        System.out.println("🔧 Admin: Getting all orders");
        System.out.println("🔧 Admin: User: " + authentication.getName());
        System.out.println("🔧 Admin: Authorities: " + authentication.getAuthorities());

        List<Order> orders = orderRepository.findAll();
        System.out.println("🔧 Admin: Found " + orders.size() + " orders");
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/active")
    public ResponseEntity<List<Order>> getActiveOrders() {
        List<Order> orders = orderRepository.findActiveOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/pickups")
    public ResponseEntity<List<PickupRequest>> getAllPickups(Authentication authentication) {
        System.out.println("🔧 Admin: Getting all pickups");
        System.out.println("🔧 Admin: User: " + authentication.getName());
        System.out.println("🔧 Admin: Authorities: " + authentication.getAuthorities());

        List<PickupRequest> pickups = pickupRequestRepository.findAll();
        System.out.println("🔧 Admin: Found " + pickups.size() + " pickups");
        return ResponseEntity.ok(pickups);
    }

    @GetMapping("/pickups/pending")
    public ResponseEntity<List<PickupRequest>> getPendingPickups() {
        List<PickupRequest> pickups = pickupRequestRepository.findByStatus(PickupRequest.PickupStatus.PENDING);
        return ResponseEntity.ok(pickups);
    }

    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueStats() {
        Map<String, Object> revenue = new HashMap<>();
        
        Double totalRevenue = orderRepository.getTotalRevenue();
        revenue.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
        Double monthlyRevenue = orderRepository.getRevenueByDateRange(monthAgo, LocalDateTime.now());
        revenue.put("monthlyRevenue", monthlyRevenue != null ? monthlyRevenue : 0.0);
        
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
        Double weeklyRevenue = orderRepository.getRevenueByDateRange(weekAgo, LocalDateTime.now());
        revenue.put("weeklyRevenue", weeklyRevenue != null ? weeklyRevenue : 0.0);
        
        LocalDateTime dayAgo = LocalDateTime.now().minusDays(1);
        Double dailyRevenue = orderRepository.getRevenueByDateRange(dayAgo, LocalDateTime.now());
        revenue.put("dailyRevenue", dailyRevenue != null ? dailyRevenue : 0.0);
        
        return ResponseEntity.ok(revenue);
    }

    @PostMapping("/users/{id}/points")
    public ResponseEntity<User> addPointsToUser(@PathVariable Long id, @RequestParam Integer points) {
        try {
            User updatedUser = userService.addPoints(id, points);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/users/top-recyclers")
    public ResponseEntity<List<User>> getTopRecyclers() {
        List<User> topUsers = userService.getTopUsersByPoints();
        return ResponseEntity.ok(topUsers);
    }

    // ========== MISSING ADMIN FUNCTIONALITIES FROM CLASS DIAGRAM ==========

    // Product validation functionality
    @PutMapping("/products/{id}/validate")
    public ResponseEntity<Product> validateProduct(@PathVariable Long id, @RequestParam boolean approved) {
        try {
            Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

            product.setIsAvailable(approved);
            Product updatedProduct = productService.updateProduct(id, product);

            // Send notification to product creator
            if (product.getCreatedBy() != null) {
                String status = approved ? "approved" : "rejected";
                notificationService.sendNotification(
                    product.getCreatedBy(),
                    "Product " + status,
                    "Your product '" + product.getName() + "' has been " + status,
                    Notification.NotificationType.INFO
                );
            }

            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Order approval functionality
    @PutMapping("/orders/{id}/approve")
    public ResponseEntity<Order> approveOrder(@PathVariable Long id) {
        try {
            Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

            order.setStatus(Order.OrderStatus.CONFIRMED);
            Order updatedOrder = orderRepository.save(order);

            // Send notification to user
            notificationService.sendOrderNotification(
                order.getUser(),
                order.getOrderNumber(),
                "approved and confirmed"
            );

            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Driver assignment functionality
    @PutMapping("/pickups/{id}/assign-driver")
    public ResponseEntity<PickupRequest> assignDriver(@PathVariable Long id, @RequestParam Long driverId) {
        try {
            PickupRequest pickup = pickupRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pickup request not found"));

            Driver driver = driverService.getDriverById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

            // Assign driver to pickup
            pickup.assignToDriver(driver);
            PickupRequest updatedPickup = pickupRequestRepository.save(pickup);

            // Update driver status
            driverService.assignPickup(driverId, pickup);

            // Send notifications
            notificationService.sendPickupNotification(
                pickup.getUser(),
                pickup.getId().toString(),
                "assigned to driver " + driver.getName()
            );

            return ResponseEntity.ok(updatedPickup);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Send notification functionality
    @PostMapping("/notifications/send")
    public ResponseEntity<Notification> sendNotification(@RequestBody NotificationRequest request) {
        try {
            User user = userService.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

            Notification notification = notificationService.sendNotification(
                user,
                request.getTitle(),
                request.getMessage(),
                Notification.NotificationType.valueOf(request.getType().toUpperCase())
            );

            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Broadcast notification to all users
    @PostMapping("/notifications/broadcast")
    public ResponseEntity<Map<String, Object>> broadcastNotification(@RequestBody BroadcastRequest request) {
        try {
            List<User> users = userService.getAllUsers();
            int sentCount = 0;

            for (User user : users) {
                notificationService.sendNotification(
                    user,
                    request.getTitle(),
                    request.getMessage(),
                    Notification.NotificationType.valueOf(request.getType().toUpperCase())
                );
                sentCount++;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Notification sent successfully");
            result.put("sentCount", sentCount);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Driver management endpoints
    @GetMapping("/drivers")
    public ResponseEntity<List<Driver>> getAllDrivers() {
        List<Driver> drivers = driverService.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }

    @PostMapping("/drivers")
    public ResponseEntity<Driver> createDriver(@RequestBody Driver driver) {
        try {
            Driver createdDriver = driverService.createDriver(driver);
            return ResponseEntity.ok(createdDriver);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/drivers/{id}")
    public ResponseEntity<Driver> updateDriver(@PathVariable Long id, @RequestBody Driver driver) {
        try {
            Driver updatedDriver = driverService.updateDriver(id, driver);
            return ResponseEntity.ok(updatedDriver);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/drivers/{id}/status")
    public ResponseEntity<Driver> updateDriverStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Driver.DriverStatus driverStatus = Driver.DriverStatus.valueOf(status.toUpperCase());
            Driver updatedDriver = driverService.updateDriverStatus(id, driverStatus);
            return ResponseEntity.ok(updatedDriver);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/drivers/{id}")
    public ResponseEntity<?> deleteDriver(@PathVariable Long id) {
        try {
            driverService.deleteDriver(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DTO classes for requests
    public static class NotificationRequest {
        private Long userId;
        private String title;
        private String message;
        private String type;

        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    public static class BroadcastRequest {
        private String title;
        private String message;
        private String type;

        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
}
