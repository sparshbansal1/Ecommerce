package com.ecommerce.controller;

import com.ecommerce.model.Cart;
import com.ecommerce.model.Payment;
import com.ecommerce.model.User;
import com.ecommerce.repository.PaymentRepository;
import com.ecommerce.service.CartService;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.RazorpayService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Payment Gateway Integration module (3.4) using Razorpay.
 *
 * Flow:
 *  1. /payment/checkout  -> create a Razorpay order for the cart total, show checkout page
 *  2. Razorpay popup     -> user pays with test cards / UPI / netbanking / wallets
 *  3. /payment/verify    -> verify the signature, mark the payment COMPLETED, clear the cart
 */
@Controller
public class PaymentController {

    private static final String CURRENT_USER = "currentUser";
    private static final String ERROR_MESSAGE = "errorMessage";

    @Autowired
    private CartService cartService;

    @Autowired
    private RazorpayService razorpayService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderService orderService;

    /** Start checkout: create the Razorpay order and open the payment page. */
    @PostMapping("/payment/checkout")
    public String checkout(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(CURRENT_USER);
        if (user == null) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Please login to checkout.");
            return "redirect:/login";
        }

        BigDecimal total = cartService.getTotal(user.getUserId());
        if (total.signum() <= 0) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Your cart is empty.");
            return "redirect:/cart";
        }

        // Razorpay works in the smallest currency unit (paise for INR)
        long amountPaise = total.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();

        String orderId;
        try {
            orderId = razorpayService.createOrder(amountPaise);
        } catch (RuntimeException e) {
            // e.g. network/SSL block reaching api.razorpay.com, or invalid keys
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE,
                    "Could not start payment: " + e.getMessage());
            return "redirect:/cart";
        }

        // Save a PENDING payment we can update after the user pays
        paymentRepository.save(new Payment(user.getUserId(), orderId, total, "PENDING"));

        model.addAttribute("keyId", razorpayService.getKeyId());
        model.addAttribute("currency", razorpayService.getCurrency());
        model.addAttribute("orderId", orderId);
        model.addAttribute("amountPaise", amountPaise);
        model.addAttribute("amountDisplay", total);
        model.addAttribute("demoMode", razorpayService.isDemoMode());
        model.addAttribute("customerName", user.getUsername());
        model.addAttribute("customerEmail", user.getEmail());
        return "checkout";
    }

    /** Razorpay calls this back after payment; verify and finalize. */
    @PostMapping("/payment/verify")
    public String verify(@RequestParam(name = "razorpay_order_id") String orderId,
                         @RequestParam(name = "razorpay_payment_id", required = false) String paymentId,
                         @RequestParam(name = "razorpay_signature", required = false) String signature,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute(CURRENT_USER);
        if (user == null) {
            return "redirect:/login";
        }

        boolean valid = razorpayService.verifySignature(orderId, paymentId, signature);
        Optional<Payment> paymentOpt = paymentRepository.findByRazorpayOrderId(orderId);

        if (valid && paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setRazorpayPaymentId(paymentId);
            payment.setPaymentStatus("COMPLETED");
            paymentRepository.save(payment);

            // Turn the paid cart into an order (snapshot items) BEFORE clearing it
            String paymentRef = (paymentId != null) ? paymentId : "DEMO-" + orderId;
            List<Cart> cartItems = cartService.getItems(user.getUserId());
            orderService.createOrder(user, cartItems, payment.getAmount(), paymentRef);

            // Payment done -> empty the cart
            cartService.clearCart(user.getUserId());

            redirectAttributes.addFlashAttribute("paymentId",
                    paymentId != null ? paymentId : "DEMO-" + orderId);
            redirectAttributes.addFlashAttribute("amount", payment.getAmount());
            return "redirect:/payment/success";
        }

        paymentOpt.ifPresent(payment -> {
            payment.setPaymentStatus("FAILED");
            paymentRepository.save(payment);
        });
        redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Payment could not be verified.");
        return "redirect:/payment/failure";
    }

    @GetMapping("/payment/success")
    public String success() {
        return "payment-success";
    }

    @GetMapping("/payment/failure")
    public String failure() {
        return "payment-failure";
    }
}
