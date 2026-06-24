package com.ecommerce.controller;

import com.ecommerce.model.User;
import com.ecommerce.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Makes shared data available to every page:
 *  - cartCount: number of items in the cart (navbar badge)
 *  - currentUser: the logged-in user, or null if anonymous (controls login/admin/logout buttons)
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private CartService cartService;

    @ModelAttribute("cartCount")
    public int cartCount(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        return (user != null) ? cartService.getTotalItems(user.getUserId()) : 0;
    }

    @ModelAttribute("currentUser")
    public User currentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }
}
