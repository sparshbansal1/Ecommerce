package com.ecommerce.service;

import com.ecommerce.model.Cart;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.User;
import com.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Order Processing module (3.3).
 *
 * Creates orders from a paid cart and lets admins read and update them.
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Turn a paid cart into an order. Called right after a successful payment,
     * before the cart is cleared. Product name/price are snapshotted.
     */
    public Order createOrder(User user, List<Cart> cartItems, BigDecimal total, String paymentRef) {
        Order order = new Order(user.getUserId(), user.getUsername(), total, paymentRef);
        for (Cart item : cartItems) {
            order.addItem(new OrderItem(
                    item.getProductId(),
                    item.getName(),
                    item.getPrice(),
                    item.getQuantity()));
        }
        return orderRepository.save(order);
    }

    /** All orders, newest first (admin list). */
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    /** Orders with a given status, newest first. */
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatusOrderByOrderDateDesc(status);
    }

    /** One order by id. */
    public Optional<Order> getOrderById(Integer orderId) {
        return orderRepository.findById(orderId);
    }

    /** Update an order's status (admin action). Returns false if not found. */
    public boolean updateStatus(Integer orderId, String status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return false;
        }
        Order order = orderOpt.get();
        order.setStatus(status);
        orderRepository.save(order);
        return true;
    }

    /** Count of orders in a given status (stat cards). */
    public long countByStatus(String status) {
        return orderRepository.countByStatus(status);
    }
}
