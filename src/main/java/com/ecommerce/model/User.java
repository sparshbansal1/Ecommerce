package com.ecommerce.model;

import jakarta.persistence.*;

/**
 * Application user for the User Management module (3.5).
 *
 * Note: the table is named "users" because USER is a reserved keyword in MySQL.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    // Stored as a hashed value, never plain text
    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String email;

    // "USER" or "ADMIN"
    @Column(nullable = false, length = 20)
    private String role;

    public User() {
    }

    // Getters and Setters
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
