package com.wastereborn.controller;

import com.wastereborn.model.Order;
import com.wastereborn.model.Payment;
import com.wastereborn.model.User;
import com.wastereborn.service.OrderService;
import com.wastereborn.service.PaymentService;
import com.wastereborn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    // Create payment for an order
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPayment(
            @RequestBody PaymentRequest request,
            Authentication authentication) {
        
        try {
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Order order = orderService.getOrderById(request.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Verify user owns the order
            if (!order.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Unauthorized access to order"));
            }

            Payment payment = paymentService.createPayment(
                    order, 
                    request.getPaymentMethod(), 
                    request.getPhoneNumber()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("paymentId", payment.getId());
            response.put("paymentReference", payment.getPaymentReference());
            response.put("status", payment.getStatus());
            response.put("amount", payment.getAmount());
            response.put("message", "Payment created successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Process MTN Mobile Money payment
    @PostMapping("/{paymentId}/process-mtn")
    public ResponseEntity<Map<String, Object>> processMTNPayment(
            @PathVariable Long paymentId,
            @RequestBody MTNPaymentRequest request,
            Authentication authentication) {
        
        try {
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Payment payment = paymentService.getPaymentById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            // Verify user owns the payment
            if (!payment.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Unauthorized access to payment"));
            }

            Payment processedPayment = paymentService.processMTNPayment(paymentId, request.getPhoneNumber());

            Map<String, Object> response = new HashMap<>();
            response.put("paymentId", processedPayment.getId());
            response.put("status", processedPayment.getStatus());
            response.put("message", getStatusMessage(processedPayment.getStatus()));
            
            if (processedPayment.getStatus() == Payment.PaymentStatus.COMPLETED) {
                response.put("deliveryEstimate", "Your order will be delivered within 2-3 business days");
                response.put("orderNumber", processedPayment.getOrder().getOrderNumber());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Process points payment
    @PostMapping("/{paymentId}/process-points")
    public ResponseEntity<Map<String, Object>> processPointsPayment(
            @PathVariable Long paymentId,
            Authentication authentication) {
        
        try {
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Payment payment = paymentService.getPaymentById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            // Verify user owns the payment
            if (!payment.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Unauthorized access to payment"));
            }

            Payment processedPayment = paymentService.processPointsPayment(paymentId);

            Map<String, Object> response = new HashMap<>();
            response.put("paymentId", processedPayment.getId());
            response.put("status", processedPayment.getStatus());
            response.put("message", getStatusMessage(processedPayment.getStatus()));
            
            if (processedPayment.getStatus() == Payment.PaymentStatus.COMPLETED) {
                response.put("deliveryEstimate", "Your order will be delivered within 2-3 business days");
                response.put("orderNumber", processedPayment.getOrder().getOrderNumber());
                
                // Get updated user points balance
                User updatedUser = userService.findById(user.getId()).orElse(user);
                response.put("remainingPoints", updatedUser.getPointsBalance());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get payment status
    @GetMapping("/{paymentId}/status")
    public ResponseEntity<Map<String, Object>> getPaymentStatus(
            @PathVariable Long paymentId,
            Authentication authentication) {
        
        try {
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Payment payment = paymentService.getPaymentById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            // Verify user owns the payment
            if (!payment.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Unauthorized access to payment"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("paymentId", payment.getId());
            response.put("status", payment.getStatus());
            response.put("amount", payment.getAmount());
            response.put("paymentMethod", payment.getPaymentMethod());
            response.put("createdAt", payment.getCreatedAt());
            response.put("message", getStatusMessage(payment.getStatus()));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get user's payment history
    @GetMapping("/history")
    public ResponseEntity<List<Payment>> getPaymentHistory(Authentication authentication) {
        try {
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Payment> payments = paymentService.getPaymentsByUser(user);
            return ResponseEntity.ok(payments);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Cancel payment
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelPayment(
            @PathVariable Long paymentId,
            Authentication authentication) {
        
        try {
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Payment payment = paymentService.getPaymentById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            // Verify user owns the payment
            if (!payment.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Unauthorized access to payment"));
            }

            Payment cancelledPayment = paymentService.cancelPayment(paymentId);

            Map<String, Object> response = new HashMap<>();
            response.put("paymentId", cancelledPayment.getId());
            response.put("status", cancelledPayment.getStatus());
            response.put("message", "Payment cancelled successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private String getStatusMessage(Payment.PaymentStatus status) {
        switch (status) {
            case PENDING:
                return "Payment is pending. Please complete the payment process.";
            case PROCESSING:
                return "Payment is being processed. Please wait...";
            case COMPLETED:
                return "Payment completed successfully! Your order has been confirmed.";
            case FAILED:
                return "Payment failed. Please try again or use a different payment method.";
            case CANCELLED:
                return "Payment was cancelled.";
            case REFUNDED:
                return "Payment has been refunded.";
            default:
                return "Unknown payment status.";
        }
    }

    // Request DTOs
    public static class PaymentRequest {
        private Long orderId;
        private Payment.PaymentMethod paymentMethod;
        private String phoneNumber;

        // Getters and setters
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }

        public Payment.PaymentMethod getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(Payment.PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    }

    public static class MTNPaymentRequest {
        private String phoneNumber;

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    }
}
