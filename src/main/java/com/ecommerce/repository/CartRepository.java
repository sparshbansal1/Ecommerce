package com.ecommerce.repository;

import com.ecommerce.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    // All cart rows for one user
    List<Cart> findByUserId(Integer userId);

    // A specific product in a specific user's cart (used to increment quantity)
    Optional<Cart> findByUserIdAndProductProductId(Integer userId, Integer productId);

    // Empty a user's cart
    @Transactional
    void deleteByUserId(Integer userId);
}
