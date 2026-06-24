package com.ecommerce;

import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) {
        // Seed default accounts (idempotent – register() skips existing usernames)
        userService.register("admin", "admin@shopease.com", "admin123", "ADMIN");
        userService.register("user", "user@shopease.com", "user123", "USER");

        // Only seed if database is empty
        if (categoryRepository.count() == 0) {

            // Create Categories
            Category electronics = new Category("Electronics");
            Category clothing = new Category("Clothing");
            Category books = new Category("Books");
            Category sports = new Category("Sports");
            Category beauty = new Category("Beauty");

            categoryRepository.save(electronics);
            categoryRepository.save(clothing);
            categoryRepository.save(books);
            categoryRepository.save(sports);
            categoryRepository.save(beauty);

            // Create Sample Products
            saveProduct("Wireless Headphones", "Premium noise-cancelling wireless headphones with 30hr battery life.", new BigDecimal("2499.00"), 50, "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400", electronics);
            saveProduct("Smartphone Pro Max", "6.7 inch AMOLED display, 5G enabled, 108MP camera.", new BigDecimal("54999.00"), 20, "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400", electronics);
            saveProduct("Laptop Ultra", "15.6 inch, Intel i7, 16GB RAM, 512GB SSD.", new BigDecimal("79999.00"), 15, "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400", electronics);
            saveProduct("Smartwatch Series 5", "Health tracking, GPS, water resistant up to 50m.", new BigDecimal("14999.00"), 35, "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400", electronics);

            saveProduct("Men's Casual T-Shirt", "100% cotton, available in multiple colors.", new BigDecimal("599.00"), 200, "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400", clothing);
            saveProduct("Women's Summer Dress", "Floral print, lightweight fabric, perfect for summers.", new BigDecimal("1299.00"), 120, "https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?w=400", clothing);
            saveProduct("Running Shoes", "Lightweight, breathable mesh, cushioned sole.", new BigDecimal("3499.00"), 80, "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400", sports);

            saveProduct("Java Programming Complete Guide", "Master Java from basics to advanced with Spring Boot.", new BigDecimal("799.00"), 150, "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400", books);
            saveProduct("The Lean Startup", "Build better products with validated learning.", new BigDecimal("499.00"), 90, "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400", books);

            saveProduct("Face Moisturizer SPF 50", "Daily moisturizer with sun protection, suitable for all skin types.", new BigDecimal("899.00"), 60, "https://images.unsplash.com/photo-1556228578-8c89e6adf883?w=400", beauty);
            saveProduct("Yoga Mat Premium", "Non-slip, eco-friendly, 6mm thick yoga mat.", new BigDecimal("1299.00"), 45, "https://images.unsplash.com/photo-1601925228001-4d8f67a2c9d8?w=400", sports);
            saveProduct("Bluetooth Speaker", "360-degree sound, waterproof, 12hr playtime.", new BigDecimal("1999.00"), 40, "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=400", electronics);

            System.out.println("✅ Sample data loaded successfully!");
        }
    }

    private void saveProduct(String name, String description, BigDecimal price, int stock, String imageUrl, Category category) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setStockQuantity(stock);
        p.setImageUrl(imageUrl);
        p.setCategory(category);
        productRepository.save(p);
    }
}
