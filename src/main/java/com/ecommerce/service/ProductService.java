package com.ecommerce.service;

import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get product by ID
    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }

    // Save (add or update) product
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // Delete product
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    // Search by name
    public List<Product> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    // Filter by category
    public List<Product> getByCategory(Integer categoryId) {
        return productRepository.findByCategoryCategoryId(categoryId);
    }

    // Search by name and category
    public List<Product> searchByNameAndCategory(String name, Integer categoryId) {
        return productRepository.findByNameContainingIgnoreCaseAndCategoryCategoryId(name, categoryId);
    }
}
