package com.ecommerce.controller;

import com.ecommerce.model.User;
import com.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Lets a logged-in user view and edit their own profile (User Management 3.5).
 *
 *  - GET  /profile                 -> show the profile page
 *  - POST /profile/update          -> change username / email
 *  - POST /profile/change-password -> change password (verifies current one)
 *
 * The session copy of the user is refreshed after every change so the navbar
 * greeting and the rest of the app stay in sync.
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private static final String CURRENT_USER = "currentUser";
    private static final String SUCCESS_MESSAGE = "successMessage";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String REDIRECT_PROFILE = "redirect:/profile";
    private static final String REDIRECT_LOGIN = "redirect:/login";

    @Autowired
    private UserService userService;

    /** Show the profile page with the current user's details. */
    @GetMapping
    public String profile(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(CURRENT_USER);
        if (user == null) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Please login to view your profile.");
            return REDIRECT_LOGIN;
        }

        // Reload fresh data so the form always reflects the database
        User fresh = userService.findById(user.getUserId()).orElse(user);
        session.setAttribute(CURRENT_USER, fresh);
        model.addAttribute("user", fresh);
        return "profile";
    }

    /** Update username and email. */
    @PostMapping("/update")
    public String updateDetails(@RequestParam String username,
                                @RequestParam(required = false) String email,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(CURRENT_USER);
        if (user == null) {
            return REDIRECT_LOGIN;
        }

        if (username == null || username.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Username cannot be empty.");
            return REDIRECT_PROFILE;
        }

        Optional<User> updated = userService.updateProfile(
                user.getUserId(),
                username.trim(),
                (email != null) ? email.trim() : null);

        if (updated.isEmpty()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "That username is already taken. Try another.");
            return REDIRECT_PROFILE;
        }

        // Refresh the session so the navbar greeting updates immediately
        session.setAttribute(CURRENT_USER, updated.get());
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Profile updated successfully.");
        return REDIRECT_PROFILE;
    }

    /** Change the password after verifying the current one. */
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(CURRENT_USER);
        if (user == null) {
            return REDIRECT_LOGIN;
        }

        if (newPassword == null || newPassword.length() < 4) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "New password must be at least 4 characters.");
            return REDIRECT_PROFILE;
        }
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "New password and confirmation do not match.");
            return REDIRECT_PROFILE;
        }

        boolean changed = userService.changePassword(user.getUserId(), currentPassword, newPassword);
        if (!changed) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Current password is incorrect.");
            return REDIRECT_PROFILE;
        }

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Password changed successfully.");
        return REDIRECT_PROFILE;
    }
}
