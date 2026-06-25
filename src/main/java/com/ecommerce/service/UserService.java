package com.ecommerce.service;

import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 * Handles user registration and login for the User Management module (3.5).
 *
 * Passwords are hashed with SHA-256 before saving (kept simple, no extra
 * libraries). In a real production app you would use BCrypt via Spring Security.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new account.
     * @return true if created, false if the username is already taken.
     */
    public boolean register(String username, String email, String password, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            return false;
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hash(password));
        user.setRole(role);
        userRepository.save(user);
        return true;
    }

    /**
     * Check username + password.
     * @return the matching User if the credentials are correct, otherwise empty.
     */
    public Optional<User> login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(hash(password)));
    }

    /** Get a fresh copy of a user by id (used to reload profile data). */
    public Optional<User> findById(Integer userId) {
        return userRepository.findById(userId);
    }

    /**
     * Update a user's profile details (username + email). The username must
     * stay unique across accounts.
     *
     * @return the saved User, or empty if the username is already taken by
     *         a different account.
     */
    public Optional<User> updateProfile(Integer userId, String username, String email) {
        Optional<User> sameName = userRepository.findByUsername(username);
        if (sameName.isPresent() && !sameName.get().getUserId().equals(userId)) {
            return Optional.empty(); // username belongs to someone else
        }
        return userRepository.findById(userId).map(user -> {
            user.setUsername(username);
            user.setEmail(email);
            return userRepository.save(user);
        });
    }

    /**
     * Change a user's password after verifying the current one.
     *
     * @return true if changed; false if the current password is wrong
     *         (or the user no longer exists).
     */
    public boolean changePassword(Integer userId, String currentPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        User user = userOpt.get();
        if (!user.getPassword().equals(hash(currentPassword))) {
            return false; // current password does not match
        }
        user.setPassword(hash(newPassword));
        userRepository.save(user);
        return true;
    }

    // Turn a plain password into a hashed hex string
    private String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to hash password", e);
        }
    }
}
