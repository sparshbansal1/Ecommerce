package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    // Landing Page
    @GetMapping("/")
    public String home(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer categoryId,
            Model model) {

        List<Product> products;

        // Filter logic
        if (search != null && !search.isEmpty() && categoryId != null) {
            products = productService.searchByNameAndCategory(search, categoryId);
        } else if (search != null && !search.isEmpty()) {
            products = productService.searchByName(search);
        } else if (categoryId != null) {
            products = productService.getByCategory(categoryId);
        } else {
            products = productService.getAllProducts();
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("search", search);
        model.addAttribute("selectedCategory", categoryId);

        return "index";
    }

    // Product Detail Page
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Integer id, Model model) {
        Optional<Product> productOpt = productService.getProductById(id);

        if (productOpt.isEmpty()) {
            return "redirect:/";
        }

        Product product = productOpt.get();

        // Find related products from the same category (exclude current, max 4)
        List<Product> relatedProducts = new ArrayList<>();
        if (product.getCategory() != null) {
            relatedProducts = productService.getByCategory(product.getCategory().getCategoryId())
                    .stream()
                    .filter(p -> !p.getProductId().equals(id))
                    .limit(4)
                    .toList();
        }

        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("categories", categoryService.getAllCategories());

        return "product-detail";
    }
}
