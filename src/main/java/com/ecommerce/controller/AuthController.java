package com.ecommerce.controller;

import com.ecommerce.model.User;
import com.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Login, signup and logout for the User Management module (3.5).
 *
 * The logged-in user is stored in the HTTP session under "currentUser".
 */
@Controller
public class AuthController {

    private static final String CURRENT_USER = "currentUser";
    private static final String ERROR_MESSAGE = "errorMessage";

    @Autowired
    private UserService userService;

    // ----- LOGIN -----

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        Optional<User> user = userService.login(username, password);

        if (user.isEmpty()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Invalid username or password.");
            return "redirect:/login";
        }

        // Save the user in the session – this is how we stay "logged in"
        session.setAttribute(CURRENT_USER, user.get());

        // Admins land on the admin dashboard, normal users go to the shop
        if ("ADMIN".equals(user.get().getRole())) {
            return "redirect:/admin/products";
        }
        return "redirect:/";
    }

    // ----- SIGNUP -----

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam(required = false) String email,
                         @RequestParam String password,
                         RedirectAttributes redirectAttributes) {

        // Role is intentionally NOT read from the form. Every public sign-up is
        // a regular customer; admins can never self-register.
        boolean created = userService.register(username, email, password);

        if (!created) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Username already taken. Try another.");
            return "redirect:/signup";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Account created! Please log in.");
        return "redirect:/login";
    }

    // ----- LOGOUT -----

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // clears the logged-in user (and the session cart)
        return "redirect:/";
    }
}
