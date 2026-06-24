package com.ecommerce.controller;

import com.ecommerce.model.Order;
import com.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Admin order management for the Order Processing module (3.3).
 *
 * All routes are under /admin/** and are already protected by
 * {@code AdminAuthInterceptor}, so only logged-in admins can reach them.
 */
@Controller
@RequestMapping("/admin/orders")
public class OrderController {

    private static final String REDIRECT_ORDERS = "redirect:/admin/orders";
    private static final String SUCCESS_MESSAGE = "successMessage";
    private static final String ERROR_MESSAGE = "errorMessage";

    @Autowired
    private OrderService orderService;

    /** List every order, optionally filtered by status. */
    @GetMapping
    public String listOrders(@RequestParam(required = false) String status, Model model) {
        List<Order> orders = (status != null && !status.isBlank())
                ? orderService.getOrdersByStatus(status)
                : orderService.getAllOrders();

        model.addAttribute("orders", orders);
        model.addAttribute("selectedStatus", status);

        // Stat cards
        model.addAttribute("pendingCount", orderService.countByStatus(Order.PENDING));
        model.addAttribute("shippedCount", orderService.countByStatus(Order.SHIPPED));
        model.addAttribute("deliveredCount", orderService.countByStatus(Order.DELIVERED));
        model.addAttribute("cancelledCount", orderService.countByStatus(Order.CANCELLED));
        return "admin/order-list";
    }

    /** View one order with its items. */
    @GetMapping("/view/{id}")
    public String viewOrder(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Order> orderOpt = orderService.getOrderById(id);
        if (orderOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Order not found!");
            return REDIRECT_ORDERS;
        }
        model.addAttribute("order", orderOpt.get());
        return "admin/order-detail";
    }

    /** Update an order's status (PENDING / SHIPPED / DELIVERED / CANCELLED). */
    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Integer id,
                               @RequestParam String status,
                               RedirectAttributes redirectAttributes) {
        boolean updated = orderService.updateStatus(id, status);
        if (updated) {
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE,
                    "Order #" + id + " marked as " + status + ".");
        } else {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Order not found!");
        }
        return REDIRECT_ORDERS;
    }
}
