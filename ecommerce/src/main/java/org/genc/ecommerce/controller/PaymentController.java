package org.genc.ecommerce.controller;

import org.genc.ecommerce.model.Payment;
import org.genc.ecommerce.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/checkout")
    public String showPaymentPage(Model model) {
        model.addAttribute("payment", new Payment());
        return "payment";
    }
    //RedirectAttributes is used in Spring MVC to pass data from one request to another during a redirect.
    @PostMapping("/process")
    public String processPayment(@ModelAttribute("payment") Payment payment, RedirectAttributes redirectAttributes) {
        try {
            paymentService.processPayment(payment);
            //A new request is created, so normal data is lost.
            //addFlashAttribute() helps us send data to the next request only.
            redirectAttributes.addFlashAttribute("paymentSuccess", true);
        } catch (Exception e) {
            // Returns the Hirarchy of exception.
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("paymentError", true);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/payment/checkout";
    }
}