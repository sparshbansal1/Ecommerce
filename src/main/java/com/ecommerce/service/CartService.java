package com.ecommerce.service;

import com.ecommerce.model.Cart;
import com.ecommerce.model.Product;
import com.ecommerce.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Shopping cart logic, now backed by the database (table "cart").
 *
 * Every method works on a specific userId, so each user has their own saved
 * cart that survives logout and is restored on the next login.
 */
@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    /** Add a product to the user's cart (or +1 if it's already there). */
    public void addToCart(Integer userId, Product product) {
        Optional<Cart> existing =
                cartRepository.findByUserIdAndProductProductId(userId, product.getProductId());

        if (existing.isPresent()) {
            Cart cart = existing.get();
            cart.setQuantity(cart.getQuantity() + 1);
            cartRepository.save(cart);
        } else {
            cartRepository.save(new Cart(userId, product, 1));
        }
    }

    /** Set an exact quantity. 0 or less removes the item. */
    public void updateQuantity(Integer userId, Integer productId, int quantity) {
        cartRepository.findByUserIdAndProductProductId(userId, productId).ifPresent(cart -> {
            if (quantity <= 0) {
                cartRepository.delete(cart);
            } else {
                cart.setQuantity(quantity);
                cartRepository.save(cart);
            }
        });
    }

    /** Remove one product from the user's cart. */
    public void removeFromCart(Integer userId, Integer productId) {
        cartRepository.findByUserIdAndProductProductId(userId, productId)
                .ifPresent(cartRepository::delete);
    }

    /** Empty the user's whole cart. */
    public void clearCart(Integer userId) {
        cartRepository.deleteByUserId(userId);
    }

    /** All items in the user's cart. */
    public List<Cart> getItems(Integer userId) {
        return cartRepository.findByUserId(userId);
    }

    /** Grand total price of the user's cart. */
    public BigDecimal getTotal(Integer userId) {
        return getItems(userId).stream()
                .map(Cart::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Total number of units in the user's cart (navbar badge). */
    public int getTotalItems(Integer userId) {
        return getItems(userId).stream()
                .mapToInt(Cart::getQuantity)
                .sum();
    }
}
