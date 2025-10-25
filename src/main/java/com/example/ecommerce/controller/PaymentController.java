package com.example.ecommerce.controller;

import com.example.ecommerce.service.StripeService;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.stripe.model.checkout.Session;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private StripeService stripeService;

    @GetMapping("/checkout")
    public String showCheckout(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        model.addAttribute("stripePublishableKey", stripeService.getPublishableKey());
        model.addAttribute("username", username);
        return "checkout";
    }

    @PostMapping("/create-checkout-session")
    @ResponseBody
    public ResponseEntity<Map<String, String>> createCheckoutSession(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not logged in"));
        }

        try {
            // Get cart items from Firestore
            Firestore firestore = FirestoreClient.getFirestore();
            DocumentSnapshot userDoc = firestore.collection("users").document(username).get().get();

            if (!userDoc.exists() || !userDoc.contains("carts")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cart is empty"));
            }

            Object cartsObj = userDoc.get("carts");
            if (!(cartsObj instanceof List<?>)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cart is empty"));
            }
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cartItems = (List<Map<String, Object>>) cartsObj;
            if (cartItems.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cart is empty"));
            }

            // Create Stripe checkout session
            String baseUrl = "http://localhost:8080";
            String successUrl = baseUrl + "/payment/success?session_id={CHECKOUT_SESSION_ID}";
            String cancelUrl = baseUrl + "/cart";

            Session stripeSession = stripeService.createCheckoutSession(cartItems, successUrl, cancelUrl);

            return ResponseEntity.ok(Map.of("sessionId", stripeSession.getId()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create checkout session: " + e.getMessage()));
        }
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam("session_id") String sessionId, HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        try {
            // Retrieve the session from Stripe
            Session stripeSession = Session.retrieve(sessionId);
            
            // Check if payment is successful (paid status or complete status)
            String paymentStatus = stripeSession.getPaymentStatus();
            String sessionStatus = stripeSession.getStatus();
            
            System.out.println("Payment Status: " + paymentStatus);
            System.out.println("Session Status: " + sessionStatus);
            
            if ("paid".equals(paymentStatus) || "complete".equals(sessionStatus)) {
                // Payment successful - clear cart and create order
                Firestore firestore = FirestoreClient.getFirestore();
                DocumentSnapshot userDoc = firestore.collection("users").document(username).get().get();
                
                if (userDoc.exists() && userDoc.contains("carts")) {
                    Object cartsObj = userDoc.get("carts");
                    if (!(cartsObj instanceof List<?>)) {
                        model.addAttribute("error", "Invalid cart data");
                        return "payment-error";
                    }
                    
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> cartItems = (List<Map<String, Object>>) cartsObj;
                    
                    // Create order record
                    Map<String, Object> orderData = new HashMap<>();
                    orderData.put("userId", username);
                    orderData.put("items", cartItems);
                    orderData.put("orderDate", new Date());
                    orderData.put("status", "paid");
                    orderData.put("stripeSessionId", sessionId);
                    orderData.put("paymentStatus", "completed");
                    
                    double total = cartItems.stream()
                        .mapToDouble(item -> getDoubleValue(item.get("price")) * getLongValue(item.get("quantity")))
                        .sum();
                    orderData.put("total", total);
                    
                    // Save order to Firestore
                    firestore.collection("orders").add(orderData);
                    
                    // Clear user's cart
                    firestore.collection("users").document(username).update("carts", new ArrayList<>());
                }
                
                model.addAttribute("sessionId", sessionId);
                model.addAttribute("message", "Payment successful! Your order has been placed.");
                return "payment-success";
            } else {
                System.out.println("Payment failed - Status: " + paymentStatus + ", Session Status: " + sessionStatus);
                model.addAttribute("error", "Payment status: " + paymentStatus + ", Session status: " + sessionStatus);
                return "payment-error";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error processing payment: " + e.getMessage());
            return "payment-error";
        }
    }

    @GetMapping("/cancel")
    public String paymentCancel() {
        return "redirect:/cart";
    }
    
    private double getDoubleValue(Object value) {
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Long) {
            return ((Long) value).doubleValue();
        } else if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else {
            return Double.parseDouble(value.toString());
        }
    }
    
    private long getLongValue(Object value) {
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Double) {
            return ((Double) value).longValue();
        } else {
            return Long.parseLong(value.toString());
        }
    }
}