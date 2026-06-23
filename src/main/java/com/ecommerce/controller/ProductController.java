package com.ecommerce.controller;

import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    // List all products (Admin view)
    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer categoryId,
            Model model) {

        List<Product> products;

        if (search != null && !search.isEmpty() && categoryId != null) {
            products = productService.searchByNameAndCategory(search, categoryId);
        } else if (search != null && !search.isEmpty()) {
            products = productService.searchByName(search);
        } else if (categoryId != null) {
            products = productService.getByCategory(categoryId);
        } else {
            products = productService.getAllProducts();
        }

        // Count in stock and out of stock for stat cards
        long inStock = products.stream().filter(p -> p.getStockQuantity() > 0).count();
        long outOfStock = products.stream().filter(p -> p.getStockQuantity() == 0).count();

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("search", search);
        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("inStock", inStock);
        model.addAttribute("outOfStock", outOfStock);

        return "admin/product-list";
    }

    // Show Add Product Form
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("pageTitle", "Add New Product");
        return "admin/product-form";
    }

    // Handle Add Product Submit
    @PostMapping("/add")
    public String addProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult result,
            @RequestParam(required = false) Integer categoryId,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("pageTitle", "Add New Product");
            return "admin/product-form";
        }

        // Set category if selected
        if (categoryId != null) {
            Optional<Category> cat = categoryService.getCategoryById(categoryId);
            cat.ifPresent(product::setCategory);
        }

        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("successMessage", "Product added successfully!");
        return "redirect:/admin/products";
    }

    // Show Edit Product Form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Product> productOpt = productService.getProductById(id);

        if (productOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found!");
            return "redirect:/admin/products";
        }

        model.addAttribute("product", productOpt.get());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("pageTitle", "Edit Product");
        return "admin/product-form";
    }

    // Handle Edit Product Submit
    @PostMapping("/edit/{id}")
    public String updateProduct(
            @PathVariable Integer id,
            @Valid @ModelAttribute("product") Product product,
            BindingResult result,
            @RequestParam(required = false) Integer categoryId,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("pageTitle", "Edit Product");
            return "admin/product-form";
        }

        product.setProductId(id);

        if (categoryId != null) {
            Optional<Category> cat = categoryService.getCategoryById(categoryId);
            cat.ifPresent(product::setCategory);
        }

        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully!");
        return "redirect:/admin/products";
    }

    // Delete Product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        return "redirect:/admin/products";
    }

    // View Product Details
    @GetMapping("/view/{id}")
    public String viewProduct(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Product> productOpt = productService.getProductById(id);

        if (productOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found!");
            return "redirect:/admin/products";
        }

        model.addAttribute("product", productOpt.get());
        return "admin/product-detail";
    }
}
