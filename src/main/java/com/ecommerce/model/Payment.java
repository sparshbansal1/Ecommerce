package com.ecommerce.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment record for the Payment Gateway Integration module (3.4).
 *
 * Stores one row per checkout attempt, including the Razorpay order/payment ids
 * so a payment can be traced back to Razorpay's dashboard.
 *
 * (Will link to an Order once the Order module 3.3 is built; for now it is
 *  tied to the user who paid.)
 */
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentId;

    @Column(nullable = false)
    private Integer userId;

    // Razorpay order id (order_XXXXXXXX) created before the popup opens
    @Column(length = 100)
    private String razorpayOrderId;

    // Razorpay payment id (pay_XXXXXXXX) returned after a successful payment
    @Column(length = 100)
    private String razorpayPaymentId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    // PENDING, COMPLETED or FAILED
    @Column(nullable = false, length = 20)
    private String paymentStatus;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    public Payment() {
    }

    public Payment(Integer userId, String razorpayOrderId, BigDecimal amount, String paymentStatus) {
        this.userId = userId;
        this.razorpayOrderId = razorpayOrderId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.paymentDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getPaymentId() { return paymentId; }
    public void setPaymentId(Integer paymentId) { this.paymentId = paymentId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }

    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
}
