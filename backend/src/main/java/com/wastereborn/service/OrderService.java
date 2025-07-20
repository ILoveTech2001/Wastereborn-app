package com.wastereborn.service;

import com.wastereborn.model.Order;
import com.wastereborn.model.OrderItem;
import com.wastereborn.model.Product;
import com.wastereborn.model.User;
import com.wastereborn.repository.OrderRepository;
import com.wastereborn.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    // Get all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Get order by ID
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    // Get orders by user
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    // Get order by order number
    public Optional<Order> getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    // Create new order
    public Order createOrder(Order order) {
        // Calculate total amount from order items
        double totalAmount = order.getOrderItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();
        
        order.setTotalAmount(totalAmount + order.getDeliveryFee());
        
        // Save order first
        Order savedOrder = orderRepository.save(order);
        
        // Save order items
        for (OrderItem item : order.getOrderItems()) {
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
            
            // Reduce product stock
            try {
                productService.reduceStock(item.getProduct().getId(), item.getQuantity());
            } catch (RuntimeException e) {
                // Handle insufficient stock
                throw new RuntimeException("Insufficient stock for product: " + item.getProduct().getName());
            }
        }
        
        // Send notification to user
        notificationService.sendOrderNotification(
            order.getUser(), 
            order.getOrderNumber(), 
            "created successfully"
        );
        
        return savedOrder;
    }

    // Update order status
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Order.OrderStatus oldStatus = order.getOrderStatus();
        order.setOrderStatus(status);

        if (status == Order.OrderStatus.SHIPPED) {
            order.setShippedDate(LocalDateTime.now());
            // Set estimated delivery date (2-3 days from shipping)
            order.setEstimatedDeliveryDate(LocalDateTime.now().plusDays(2));
        } else if (status == Order.OrderStatus.DELIVERED) {
            order.setDeliveredDate(LocalDateTime.now());
            
            // Award points to user for completed order (1 point per 100 FCFA)
            int pointsToAward = (int) (order.getTotalAmount() / 100);
            if (pointsToAward > 0) {
                userService.addPoints(order.getUser().getId(), pointsToAward);
            }
        } else if (status == Order.OrderStatus.CANCELLED) {
            // Restore product stock
            for (OrderItem item : order.getOrderItems()) {
                productService.updateStock(item.getProduct().getId(), item.getQuantity());
            }
        }

        Order savedOrder = orderRepository.save(order);
        
        // Send notification if status changed
        if (!oldStatus.equals(status)) {
            notificationService.sendOrderNotification(
                order.getUser(), 
                order.getOrderNumber(), 
                status.toString().toLowerCase()
            );
        }
        
        return savedOrder;
    }

    // Update payment status
    public Order updatePaymentStatus(Long orderId, Order.PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setPaymentStatus(paymentStatus);
        
        if (paymentStatus == Order.PaymentStatus.PAID && 
            order.getOrderStatus() == Order.OrderStatus.PENDING) {
            order.setOrderStatus(Order.OrderStatus.CONFIRMED);
        }

        return orderRepository.save(order);
    }

    // Cancel order
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getOrderStatus() == Order.OrderStatus.DELIVERED ||
            order.getOrderStatus() == Order.OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel order with status: " + order.getOrderStatus());
        }

        return updateOrderStatus(orderId, Order.OrderStatus.CANCELLED);
    }

    // Get orders by status
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    // Get orders by date range
    public List<Order> getOrdersByDateRange(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByCreatedAtBetween(start, end);
    }

    // Get total revenue
    public Double getTotalRevenue() {
        return orderRepository.getTotalRevenue();
    }

    // Get revenue by date range
    public Double getRevenueByDateRange(LocalDateTime start, LocalDateTime end) {
        return orderRepository.getRevenueByDateRange(start, end);
    }

    // Get order count by date range
    public Long getOrderCountByDateRange(LocalDateTime start, LocalDateTime end) {
        return orderRepository.countOrdersByDateRange(start, end);
    }

    // Delete order (admin only)
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getOrderStatus() != Order.OrderStatus.CANCELLED) {
            throw new RuntimeException("Can only delete cancelled orders");
        }

        orderRepository.deleteById(orderId);
    }

    // Get pending orders (for admin)
    public List<Order> getPendingOrders() {
        return orderRepository.findByStatus(Order.OrderStatus.PENDING);
    }

    // Get recent orders (for admin dashboard)
    public List<Order> getRecentOrders(int limit) {
        return orderRepository.findAll().stream()
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .limit(limit)
                .toList();
    }

    // Update order delivery information
    public Order updateDeliveryInfo(Long orderId, String deliveryAddress, String deliveryCity, String deliveryPhone) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getOrderStatus() != Order.OrderStatus.PENDING && 
            order.getOrderStatus() != Order.OrderStatus.CONFIRMED) {
            throw new RuntimeException("Cannot update delivery info for order with status: " + order.getOrderStatus());
        }

        order.setDeliveryAddress(deliveryAddress);
        order.setDeliveryCity(deliveryCity);
        order.setDeliveryPhone(deliveryPhone);

        return orderRepository.save(order);
    }
}
