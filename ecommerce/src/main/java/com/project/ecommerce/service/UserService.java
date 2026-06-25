package com.project.ecommerce.service;


import com.project.ecommerce.model.User;
import com.project.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
public class UserService {

    
    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    
    public User registerUser(User userData) {
        // First, we check if the username or email is already being used by someone else.
        if (userRepository.existsByUsername(userData.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(userData.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        if (userData.getRole() == null) {
            userData.setRole(User.Role.CUSTOMER);
        }

        userData.setPassword(passwordEncoder.encode(userData.getPassword()));

        // If everything is okay, we save the new user to the database.
        return userRepository.save(userData);
    }

    public User loginUser(String username, String password) {
        // We look for a user with the given username.
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Supports BCrypt and gracefully upgrades older plain-text passwords on successful login.
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }

            if (user.getPassword().equals(password)) {
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
                return user;
            }

            // If the password doesn't match, we throw an error.
            throw new RuntimeException("Error: Invalid password!");
        } else {
            throw new RuntimeException("Error: User not found with username: " + username);
        }
    }

    
    public User getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User not found with id: " + userId));
    }

    
    public User updateUserProfile(Long userId, User userData) {
        // First, we get the user's current data from the database.
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User not found with id: " + userId));

        // Then, we update the fields with the new information.
        existingUser.setUsername(userData.getUsername());
        existingUser.setAddress(userData.getAddress());
        existingUser.setMobile(userData.getMobile());

        return userRepository.save(existingUser);
    }
}
