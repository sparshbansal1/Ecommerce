package com.ecommerce.repository;

import com.ecommerce.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    // Look up a payment by its Razorpay order id (used during verification)
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
}
