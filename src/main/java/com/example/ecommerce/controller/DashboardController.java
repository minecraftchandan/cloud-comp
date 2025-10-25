package com.example.ecommerce.controller;

import com.example.ecommerce.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private ProductRepository productRepository; // MongoDB repo for products

    // Home Page (checks session)
    @GetMapping("/home")
    public String homePage(HttpSession session, Model model) {
        return checkSessionAndReturn("home", session, model);
    }

    //dashboard page
    @GetMapping("/Dashboard")
    public String   DashboardPage(HttpSession session, Model model) {
        return checkSessionAndReturn("Dashboard", session, model);
    }

    // Products Page (checks session)
    @GetMapping("/products")
    public String productsPage(HttpSession session, Model model) {
        return checkSessionAndReturn("products", session, model);
    }

    // Cart Page (checks session)
    @GetMapping("/cart")
    public String cartPage(HttpSession session, Model model) {
        return checkSessionAndReturn("cart", session, model);
    }

    // Orders Page (checks session)
    @GetMapping("/orders")
    public String ordersPage(HttpSession session, Model model) {
        return checkSessionAndReturn("orders", session, model);
    }

    // Profile Page (checks session)
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        return checkSessionAndReturn("profile", session, model);
    }

    // Admin Dashboard (only accessible by Admin role)
    @GetMapping("/admindashboard")
    public String adminDashboard(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        
        // Double check: both username and role must be admin
        if (!"admin".equals(username) || !isAdmin(session)) {
            return "redirect:/login"; // Redirect non-admin users to login
        }

        long productCount = productRepository.count();

        model.addAttribute("productCount", productCount);
        model.addAttribute("username", username);

        return "admindashboard"; // Load Admin Dashboard Page
    }

    // Helper Method: Check if user is logged in and pass role for navbar visibility
    private String checkSessionAndReturn(String page, HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");

        if (username == null) {
            return "redirect:/"; // Redirect to login page if not logged in
        }

        model.addAttribute("username", username);
        model.addAttribute("role", role); // Pass role for navbar button visibility

        return page; // Load requested page
    }

    // Helper Method: Check if user is Admin
    private boolean isAdmin(HttpSession session) {
        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");
        return "admin".equals(username) && "admin".equalsIgnoreCase(role);
    }
}
