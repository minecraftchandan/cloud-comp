package com.example.ecommerce.repository;

import com.example.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    // Find all products with a specific category
    List<Product> findByCategory(String category);
    
    // Find all products with a specific category with pagination
    Page<Product> findByCategory(String category, Pageable pageable);
    
    // Search products by name
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    // Search products by name and category
    Page<Product> findByNameContainingIgnoreCaseAndCategory(String name, String category, Pageable pageable);
}
