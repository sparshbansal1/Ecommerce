package com.project.ecommerce.controlller;

import com.project.ecommerce.model.User;
import com.project.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User userData, Model model) {
        try {
            userService.registerUser(userData);
            // If everything goes well, send them to the login page.
            return "redirect:/login";
        } catch (RuntimeException e) {
            // If something goes wrong (like the username is already taken), show an error.
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    // This just shows the login page when a user wants to sign in.
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // This checks if the username and password are correct.
    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        try {
            User user = userService.loginUser(username, password);
            if (user != null) {
                // If they are correct, we'll remember who they are for this session.
                session.setAttribute("userId", user.getUserId());
                User.Role role = user.getRole() == null ? User.Role.CUSTOMER : user.getRole();
                session.setAttribute("role", role.name());

                if (User.Role.ADMIN.equals(role)) {
                    return "redirect:/admin/dashboard";
                }

                // And send non-admin users to their profile page.
                return "redirect:/profile";
            } else {
                // If not, show an error.
                model.addAttribute("error", "Invalid login credentials");
                return "login";
            }
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId == null) {
            return "redirect:/login";
        }

        if (!"ADMIN".equals(role)) {
            return "redirect:/profile";
        }

        return "admin-dashboard";
    }

    @GetMapping("/profile")
    public String userProfile(HttpSession session, Model model) {
        // First, make sure the user is actually logged in.
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            // If not, send them to the login page.
            return "redirect:/login";
        }
        try {
            // Get the user's info and show it on the page.
            User user = userService.getUserProfile(userId);
            model.addAttribute("user", user);
            return "profile";
        } catch (RuntimeException e) {
            // If we can't find the user for some reason, back to login.
            return "redirect:/login";
        }
    }

    @PostMapping("/profile/update")
    public String updateUserProfile(@ModelAttribute("user") User userData,
                                    HttpSession session,
                                    Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        try {
            // Save the changes.
            userService.updateUserProfile(userId, userData);
            // Go back to the profile page to see the new info.
            return "redirect:/profile";
        } catch (RuntimeException e) {
            // If something went wrong, show an error.
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", userData);
            return "profile";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // End the session and forget the user.
        session.invalidate();
        // Send them back to the login page.
        return "redirect:/login";
    }
}
