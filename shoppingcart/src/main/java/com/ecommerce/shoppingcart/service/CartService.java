package com.ecommerce.shoppingcart.service;

import com.ecommerce.shoppingcart.model.Cart;
import com.ecommerce.shoppingcart.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    // Add a product to the cart
    public Cart addToCart(Cart cart) {
        return cartRepository.save(cart);
    }

    // Remove a specific cart item by cartId
    public void removeFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    // Get all cart items for a user
    public List<Cart> getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    // Calculate total price of all items in the cart
    public Double calculateTotal(Long userId) {
        List<Cart> items = cartRepository.findByUserId(userId);
        return items.stream()
                .mapToDouble(Cart::getSubtotal)
                .sum();
    }

    // Clear entire cart for a user
    public void clearCart(Long userId) {
        cartRepository.deleteByUserId(userId);
    }
}
