package com.ecommerce.shoppingcart.controller;

import com.ecommerce.shoppingcart.model.Cart;
import com.ecommerce.shoppingcart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // Show cart page for a user
    @GetMapping("/{userId}")
    public String getCartDetails(@PathVariable Long userId, Model model) {
        List<Cart> cartItems = cartService.getCartByUserId(userId);
        Double total = cartService.calculateTotal(userId);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("userId", userId);

        return "cart"; // maps to cart.html
    }

    // Show the Add to Cart form
    @GetMapping("/add/{userId}")
    public String showAddToCartForm(@PathVariable Long userId, Model model) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        model.addAttribute("cart", cart);
        model.addAttribute("userId", userId);
        return "add-to-cart"; // maps to add-to-cart.html
    }

    // Handle form submission to add item
    @PostMapping("/add")
    public String addToCart(@ModelAttribute Cart cart) {
        cartService.addToCart(cart);
        return "redirect:/cart/" + cart.getUserId();
    }

    // Remove a specific item from cart
    @GetMapping("/remove/{cartId}/{userId}")
    public String removeFromCart(@PathVariable Long cartId, @PathVariable Long userId) {
        cartService.removeFromCart(cartId);
        return "redirect:/cart/" + userId;
    }

    // Clear entire cart for a user
    @GetMapping("/clear/{userId}")
    public String clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return "redirect:/cart/" + userId;
    }
}
