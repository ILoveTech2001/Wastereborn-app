package com.wastereborn.controller;

import com.wastereborn.model.Order;
import com.wastereborn.model.User;
import com.wastereborn.service.OrderService;
import com.wastereborn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    // Create new order
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestBody Map<String, Object> orderRequest,
            Authentication authentication) {

        System.out.println("📦 Creating new order...");
        System.out.println("🔑 User: " + authentication.getName());
        System.out.println("📋 Order Request: " + orderRequest);

        try {
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            System.out.println("✅ User found: " + user.getEmail());

            // Extract order details
            String deliveryAddress = (String) orderRequest.get("deliveryAddress");
            String deliveryCity = (String) orderRequest.get("deliveryCity");
            String deliveryPhone = (String) orderRequest.get("deliveryPhone");
            String paymentMethod = (String) orderRequest.get("paymentMethod");

            // Debug total amount extraction
            Object totalAmountObj = orderRequest.get("totalAmount");
            System.out.println("🔍 Total Amount Object: " + totalAmountObj + " (Type: " +
                (totalAmountObj != null ? totalAmountObj.getClass().getSimpleName() : "null") + ")");

            Double totalAmount = null;
            if (totalAmountObj != null) {
                if (totalAmountObj instanceof Number) {
                    totalAmount = ((Number) totalAmountObj).doubleValue();
                } else if (totalAmountObj instanceof String) {
                    try {
                        totalAmount = Double.parseDouble((String) totalAmountObj);
                    } catch (NumberFormatException e) {
                        System.err.println("❌ Cannot parse totalAmount string: " + totalAmountObj);
                    }
                }
            }

            System.out.println("📋 Order Details:");
            System.out.println("   - Address: " + deliveryAddress);
            System.out.println("   - City: " + deliveryCity);
            System.out.println("   - Phone: " + deliveryPhone);
            System.out.println("   - Payment Method: " + paymentMethod);
            System.out.println("   - Total Amount: " + totalAmount);

            // Validate total amount
            if (totalAmount == null || totalAmount <= 0) {
                String errorMsg = "Invalid total amount: " + totalAmount + ". Must be greater than 0.";
                System.err.println("❌ " + errorMsg);
                throw new RuntimeException(errorMsg);
            }

            // Create order
            Order order = new Order(user, deliveryAddress, totalAmount);
            order.setDeliveryCity(deliveryCity);
            order.setDeliveryPhone(deliveryPhone);
            order.setDeliveryFee(1000.0); // Standard delivery fee

            // Convert payment method string to enum
            try {
                Order.PaymentMethod paymentMethodEnum = Order.PaymentMethod.valueOf(paymentMethod);
                order.setPaymentMethod(paymentMethodEnum);
                System.out.println("✅ Payment method set: " + paymentMethodEnum);
            } catch (IllegalArgumentException e) {
                System.err.println("❌ Invalid payment method: " + paymentMethod);
                throw new RuntimeException("Invalid payment method: " + paymentMethod + ". Valid options: " +
                    java.util.Arrays.toString(Order.PaymentMethod.values()));
            }

            Order savedOrder = orderService.createOrder(order);

            System.out.println("✅ Order created successfully with ID: " + savedOrder.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Order created successfully");
            response.put("orderId", savedOrder.getId());
            response.put("orderNumber", savedOrder.getOrderNumber());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error creating order: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to create order: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Get user's orders
    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>> getUserOrders(Authentication authentication) {
        try {
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Order> orders = orderService.getOrdersByUser(user);
            
            System.out.println("📦 Returning " + orders.size() + " orders for user: " + user.getEmail());
            
            return ResponseEntity.ok(orders);

        } catch (Exception e) {
            System.err.println("❌ Error fetching user orders: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Get order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable Long orderId,
            Authentication authentication) {
        
        try {
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Order order = orderService.getOrderById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Verify user owns the order
            if (!order.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok(order);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get order by order number
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<Order> getOrderByNumber(
            @PathVariable String orderNumber,
            Authentication authentication) {
        
        try {
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Order order = orderService.getOrderByNumber(orderNumber)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Verify user owns the order
            if (!order.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok(order);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update order status (for testing purposes)
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        
        try {
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Order order = orderService.getOrderById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Verify user owns the order
            if (!order.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Unauthorized access to order"));
            }

            String statusStr = request.get("status");
            Order.OrderStatus status = Order.OrderStatus.valueOf(statusStr.toUpperCase());
            
            Order updatedOrder = orderService.updateOrderStatus(orderId, status);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", updatedOrder.getId());
            response.put("status", updatedOrder.getOrderStatus());
            response.put("message", "Order status updated successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Cancel order
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @PathVariable Long orderId,
            Authentication authentication) {
        
        try {
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Order order = orderService.getOrderById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Verify user owns the order
            if (!order.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Unauthorized access to order"));
            }

            Order cancelledOrder = orderService.cancelOrder(orderId);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", cancelledOrder.getId());
            response.put("status", cancelledOrder.getOrderStatus());
            response.put("message", "Order cancelled successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
