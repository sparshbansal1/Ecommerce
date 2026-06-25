package com.ecommerce.controller;

import com.ecommerce.model.Order;
import com.ecommerce.model.User;
import com.ecommerce.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Customer-facing order tracking ("My Orders") for the Order Processing module (3.3).
 *
 * Lets a logged-in user view their own order history and track each order's
 * current status. Read-only: only admins (via {@link OrderController}) can
 * change an order's status.
 */
@Controller
@RequestMapping("/my-orders")
public class MyOrderController {

    private static final String CURRENT_USER = "currentUser";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String REDIRECT_LOGIN = "redirect:/login";

    @Autowired
    private OrderService orderService;

    /** List the current user's orders, newest first. */
    @GetMapping
    public String myOrders(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(CURRENT_USER);
        if (user == null) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Please login to view your orders.");
            return REDIRECT_LOGIN;
        }

        List<Order> orders = orderService.getOrdersByUser(user.getUserId());
        model.addAttribute("orders", orders);
        return "my-orders";
    }

    /** View one of the current user's orders. Ownership is enforced. */
    @GetMapping("/{id}")
    public String myOrderDetail(@PathVariable Integer id, HttpSession session,
                                Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(CURRENT_USER);
        if (user == null) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Please login to view your orders.");
            return REDIRECT_LOGIN;
        }

        Optional<Order> orderOpt = orderService.getOrderById(id);
        // Only the owner may view their own order
        if (orderOpt.isEmpty() || !orderOpt.get().getUserId().equals(user.getUserId())) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Order not found.");
            return "redirect:/my-orders";
        }

        model.addAttribute("order", orderOpt.get());
        return "my-order-detail";
    }
}
