package com.example.ecommerce.controller;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ✅ Public or protected by frontend
    @GetMapping
    public Map<String, Object> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "18") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage;
        
        if (search != null && !search.isEmpty()) {
            if (category != null && !category.isEmpty()) {
                productPage = productRepository.findByNameContainingIgnoreCaseAndCategory(search, category, pageable);
            } else {
                productPage = productRepository.findByNameContainingIgnoreCase(search, pageable);
            }
        } else if (category != null && !category.isEmpty()) {
            productPage = productRepository.findByCategory(category, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("products", productPage.getContent());
        response.put("currentPage", productPage.getNumber());
        response.put("totalPages", productPage.getTotalPages());
        response.put("totalElements", productPage.getTotalElements());
        
        return response;
    }

    // ✅ Public or protected by frontend
    @GetMapping("/categories")
    public Set<String> getAllCategories() {
        return productRepository.findAll().stream()
                .map(Product::getCategory)
                .filter(category -> category != null && !category.isEmpty())
                .collect(Collectors.toSet());
    }
}


