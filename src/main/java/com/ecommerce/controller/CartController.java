package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.service.CartService;
import com.ecommerce.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Shopping Cart Module (3.2) – database backed, login required.
 *
 * If a visitor is not logged in, any cart action sends them to the login page.
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    private static final String REDIRECT_CART = "redirect:/cart";
    private static final String REDIRECT_LOGIN = "redirect:/login";
    private static final String SUCCESS_MESSAGE = "successMessage";
    private static final String ERROR_MESSAGE = "errorMessage";

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    /** The logged-in user's id, or null if nobody is logged in. */
    private Integer currentUserId(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        return (user != null) ? user.getUserId() : null;
    }

    /** Show the cart page (login required). */
    @GetMapping
    public String viewCart(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Integer userId = currentUserId(session);
        if (userId == null) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Please login to view your cart.");
            return REDIRECT_LOGIN;
        }
        model.addAttribute("cartItems", cartService.getItems(userId));
        model.addAttribute("total", cartService.getTotal(userId));
        model.addAttribute("totalItems", cartService.getTotalItems(userId));
        return "cart";
    }

    /** Add a product to the cart. Asks for login first if not signed in. */
    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable Integer id, HttpSession session,
                            RedirectAttributes redirectAttributes) {
        Integer userId = currentUserId(session);
        if (userId == null) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Please login to add items to your cart.");
            return REDIRECT_LOGIN;
        }

        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Product not found!");
            return REDIRECT_CART;
        }

        cartService.addToCart(userId, productOpt.get());
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE,
                productOpt.get().getName() + " added to cart!");
        return REDIRECT_CART;
    }

    /** Change the quantity of a product in the cart. */
    @PostMapping("/update/{id}")
    public String updateQuantity(@PathVariable Integer id,
                                 @RequestParam int quantity,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Integer userId = currentUserId(session);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }
        cartService.updateQuantity(userId, id, quantity);
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Cart updated.");
        return REDIRECT_CART;
    }

    /** Remove a single product from the cart. */
    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Integer id, HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Integer userId = currentUserId(session);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }
        cartService.removeFromCart(userId, id);
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Item removed from cart.");
        return REDIRECT_CART;
    }

    /** Empty the whole cart. */
    @GetMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = currentUserId(session);
        if (userId == null) {
            return REDIRECT_LOGIN;
        }
        cartService.clearCart(userId);
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Cart cleared.");
        return REDIRECT_CART;
    }
}
