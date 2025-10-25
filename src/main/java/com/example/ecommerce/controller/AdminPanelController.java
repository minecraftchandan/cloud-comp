package com.example.ecommerce.controller;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminPanelController {

    @Autowired
    private ProductRepository productRepository;

    // Admin Products Management
    @GetMapping("/products")
    public String showProducts(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        return "admin-product";
    }

    // Admin Orders Management
    @GetMapping("/orders")
    public String showOrders(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        return "admin-order";
    }

    // Product Management Actions
    @PostMapping("/manageProduct")
    public String manageProduct(@RequestParam String actionType,
                                @RequestParam(required = false) String id,
                                @RequestParam(required = false) String name,
                                @RequestParam(required = false) Double price,
                                @RequestParam(required = false) String pictureUrl,
                                @RequestParam(required = false) String category,
                                @RequestParam(required = false) String description,
                                HttpSession session) {
        
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            switch (actionType) {
                case "add":
                    if (name != null && price != null && pictureUrl != null && category != null) {
                        Product newProduct = new Product(name, price, pictureUrl, category, description);
                        productRepository.save(newProduct);
                    }
                    break;

                case "update":
                    if (id != null) {
                        Optional<Product> optionalProduct = productRepository.findById(id);
                        if (optionalProduct.isPresent()) {
                            Product product = optionalProduct.get();
                            if (name != null) product.setName(name);
                            if (price != null) product.setPrice(price);
                            if (pictureUrl != null) product.setPictureUrl(pictureUrl);
                            if (category != null) product.setCategory(category);
                            if (description != null) product.setDescription(description);
                            productRepository.save(product);
                        }
                    }
                    break;

                case "delete":
                    if (id != null) {
                        productRepository.deleteById(id);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/admin/products";
    }

    private boolean isAdmin(HttpSession session) {
        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");
        return "admin".equals(username) || "admin".equalsIgnoreCase(role);
    }
}