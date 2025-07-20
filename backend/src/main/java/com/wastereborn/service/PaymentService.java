package com.wastereborn.service;

import com.wastereborn.model.Order;
import com.wastereborn.model.Payment;
import com.wastereborn.model.User;
import com.wastereborn.repository.PaymentRepository;
import com.wastereborn.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Value("${mtn.api.base-url:https://sandbox.momodeveloper.mtn.com}")
    private String mtnApiBaseUrl;

    @Value("${mtn.api.subscription-key:your-subscription-key}")
    private String mtnSubscriptionKey;

    @Value("${mtn.api.target-environment:sandbox}")
    private String mtnTargetEnvironment;

    private final RestTemplate restTemplate = new RestTemplate();

    // Create a new payment
    public Payment createPayment(Order order, Payment.PaymentMethod paymentMethod, String phoneNumber) {
        Payment payment = new Payment(order, order.getUser(), order.getTotalAmount(), paymentMethod);
        
        if (paymentMethod == Payment.PaymentMethod.MTN_MOBILE_MONEY && phoneNumber != null) {
            payment.setExternalReference(phoneNumber);
        }
        
        return paymentRepository.save(payment);
    }

    // Process MTN Mobile Money payment
    public Payment processMTNPayment(Long paymentId, String phoneNumber) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(Payment.PaymentStatus.PROCESSING);
        payment.setExternalReference(phoneNumber);
        paymentRepository.save(payment);

        try {
            // Simulate MTN Mobile Money API call
            Map<String, Object> mtnResponse = callMTNAPI(payment, phoneNumber);
            
            if (mtnResponse.get("status").equals("SUCCESSFUL")) {
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
                payment.setPaymentDate(LocalDateTime.now());
                payment.setProviderResponse(mtnResponse.toString());
                
                // Update order status
                if (payment.getOrder() != null) {
                    Order order = payment.getOrder();
                    order.setPaymentStatus(Order.PaymentStatus.PAID);
                    order.setOrderStatus(Order.OrderStatus.CONFIRMED);
                    orderRepository.save(order);
                    
                    // Send notification to user
                    notificationService.sendOrderNotification(
                        order.getUser(), 
                        order.getOrderNumber(), 
                        "confirmed - payment received"
                    );
                }
                
            } else {
                payment.setStatus(Payment.PaymentStatus.FAILED);
                payment.setProviderResponse(mtnResponse.toString());
            }
            
        } catch (Exception e) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setProviderResponse("Error: " + e.getMessage());
        }

        return paymentRepository.save(payment);
    }

    // Simulate MTN Mobile Money API call
    private Map<String, Object> callMTNAPI(Payment payment, String phoneNumber) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // In a real implementation, you would call the actual MTN API
            // For now, we'll simulate the API call
            
            System.out.println("🏦 Simulating MTN Mobile Money API call...");
            System.out.println("📱 Phone: " + phoneNumber);
            System.out.println("💰 Amount: " + payment.getAmount() + " FCFA");
            System.out.println("🔗 Reference: " + payment.getPaymentReference());
            
            // Simulate API delay
            Thread.sleep(2000);
            
            // Simulate 90% success rate
            boolean isSuccessful = Math.random() > 0.1;
            
            if (isSuccessful) {
                response.put("status", "SUCCESSFUL");
                response.put("transactionId", "MTN" + System.currentTimeMillis());
                response.put("message", "Payment processed successfully");
                response.put("timestamp", LocalDateTime.now().toString());
            } else {
                response.put("status", "FAILED");
                response.put("error", "Insufficient funds or invalid phone number");
                response.put("timestamp", LocalDateTime.now().toString());
            }
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("error", e.getMessage());
        }
        
        return response;
    }

    // Process points payment
    public Payment processPointsPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        User user = payment.getUser();
        int pointsRequired = (int) (payment.getAmount() / 10); // 10 FCFA = 1 point

        if (user.getPointsBalance() < pointsRequired) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setProviderResponse("Insufficient points. Required: " + pointsRequired + ", Available: " + user.getPointsBalance());
            return paymentRepository.save(payment);
        }

        try {
            // Deduct points from user
            userService.deductPoints(user.getId(), pointsRequired);
            
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setProviderResponse("Points payment successful. Deducted: " + pointsRequired + " points");
            
            // Update order status
            if (payment.getOrder() != null) {
                Order order = payment.getOrder();
                order.setPaymentStatus(Order.PaymentStatus.PAID);
                order.setOrderStatus(Order.OrderStatus.CONFIRMED);
                orderRepository.save(order);
                
                // Send notification
                notificationService.sendOrderNotification(
                    order.getUser(), 
                    order.getOrderNumber(), 
                    "confirmed - paid with points"
                );
            }
            
        } catch (Exception e) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setProviderResponse("Error processing points payment: " + e.getMessage());
        }

        return paymentRepository.save(payment);
    }

    // Get payment by ID
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    // Get payments by user
    public List<Payment> getPaymentsByUser(User user) {
        return paymentRepository.findByUserOrderByCreatedAtDesc(user);
    }

    // Get payment by reference
    public Optional<Payment> getPaymentByReference(String reference) {
        return paymentRepository.findByPaymentReference(reference);
    }

    // Cancel payment
    public Payment cancelPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() == Payment.PaymentStatus.PENDING || 
            payment.getStatus() == Payment.PaymentStatus.PROCESSING) {
            payment.setStatus(Payment.PaymentStatus.CANCELLED);
            return paymentRepository.save(payment);
        }

        throw new RuntimeException("Cannot cancel payment with status: " + payment.getStatus());
    }

    // Get payment status
    public Payment.PaymentStatus getPaymentStatus(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return payment.getStatus();
    }

    // Retry failed payment
    public Payment retryPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != Payment.PaymentStatus.FAILED) {
            throw new RuntimeException("Can only retry failed payments");
        }

        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setProviderResponse(null);
        return paymentRepository.save(payment);
    }
}
