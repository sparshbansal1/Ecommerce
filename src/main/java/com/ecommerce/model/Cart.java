package com.ecommerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * A row in a user's saved shopping cart (Shopping Cart Module 3.2).
 *
 * Stored in the "cart" table so each user's cart is persisted in the database
 * and restored the next time they log in.
 */
@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cartId;

    // Which user this cart row belongs to
    @Column(nullable = false)
    private Integer userId;

    // The product in the cart (EAGER by default, so product details are ready for the page)
    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    public Cart() {
    }

    public Cart(Integer userId, Product product, int quantity) {
        this.userId = userId;
        this.product = product;
        this.quantity = quantity;
    }

    // ----- Convenience getters so the cart page can use item.name, item.price, etc. -----

    @Transient
    public Integer getProductId() {
        return product != null ? product.getProductId() : null;
    }

    @Transient
    public String getName() {
        return product != null ? product.getName() : null;
    }

    @Transient
    public BigDecimal getPrice() {
        return product != null ? product.getPrice() : null;
    }

    @Transient
    public String getImageUrl() {
        return product != null ? product.getImageUrl() : null;
    }

    /** price * quantity */
    @Transient
    public BigDecimal getSubtotal() {
        if (product == null || product.getPrice() == null) {
            return BigDecimal.ZERO;
        }
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    // ----- Standard getters and setters -----

    public Integer getCartId() { return cartId; }
    public void setCartId(Integer cartId) { this.cartId = cartId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
