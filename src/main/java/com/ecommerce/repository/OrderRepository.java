package com.ecommerce.repository;

import com.ecommerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Newest orders first (admin order list)
    List<Order> findAllByOrderByOrderDateDesc();

    // Orders filtered by status (admin filter)
    List<Order> findByStatusOrderByOrderDateDesc(String status);

    // A specific user's orders (for a future "My Orders" page)
    List<Order> findByUserIdOrderByOrderDateDesc(Integer userId);

    // For the dashboard stat cards
    long countByStatus(String status);
}
