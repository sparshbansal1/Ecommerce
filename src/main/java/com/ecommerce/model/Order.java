package com.ecommerce.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A customer order for the Order Processing module (3.3).
 *
 * Created automatically when a payment succeeds. The table is named "orders"
 * because ORDER is a reserved keyword in SQL.
 */
@Entity
@Table(name = "orders")
public class Order {

    /** Allowed order statuses. */
    public static final String PENDING = "PENDING";
    public static final String SHIPPED = "SHIPPED";
    public static final String DELIVERED = "DELIVERED";
    public static final String CANCELLED = "CANCELLED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @Column(nullable = false)
    private Integer userId;

    // Snapshot of the buyer's username so admins can see who ordered
    @Column(length = 50)
    private String username;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    // PENDING, SHIPPED, DELIVERED or CANCELLED
    @Column(nullable = false, length = 20)
    private String status;

    // Razorpay payment id for reference
    @Column(length = 100)
    private String paymentRef;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
    }

    public Order(Integer userId, String username, BigDecimal totalAmount, String paymentRef) {
        this.userId = userId;
        this.username = username;
        this.totalAmount = totalAmount;
        this.paymentRef = paymentRef;
        this.status = PENDING;
        this.orderDate = LocalDateTime.now();
    }

    /** Add an item and keep both sides of the relationship in sync. */
    public void addItem(OrderItem item) {
        item.setOrder(this);
        this.items.add(item);
    }

    /** Total number of units in this order. */
    @Transient
    public int getItemCount() {
        return items.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    // Getters and Setters
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentRef() { return paymentRef; }
    public void setPaymentRef(String paymentRef) { this.paymentRef = paymentRef; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
