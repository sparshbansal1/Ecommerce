package com.ecommerce.shoppingcart.repository;

import com.ecommerce.shoppingcart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Find all cart items for a specific user
    List<Cart> findByUserId(Long userId);

    // Delete all cart items for a specific user (used after order is placed)
    void deleteByUserId(Long userId);
}
