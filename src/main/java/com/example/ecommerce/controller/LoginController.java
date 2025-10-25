package com.example.ecommerce.controller;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class LoginController {


    @GetMapping("/")
    public String showLoginPage() {
        return "login";
    }
    
    @GetMapping("/mobile-login")
    public String showMobileLoginPage() {
        return "mobile-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        boolean validUser = false;
        String role = "";

        if ("not".equals(username) && "mod".equals(password)) {
            role = "USER";
            validUser = true;
        } else if ("admin".equals(username) && "mod".equals(password)) {
            role = "ADMIN";
            validUser = true;
        } else if ("notmod".equals(username) && "loggers".equals(password)) {
            validUser = true;
            role = "USER";
        } else {
            try {
                Firestore firestore = FirestoreClient.getFirestore();

                DocumentSnapshot userDoc = firestore.collection("users").document(username).get().get();
                if (userDoc.exists()) {
                    String storedPassword = userDoc.getString("password");
                    if (storedPassword != null && storedPassword.equals(password)) {
                        validUser = true;
                        role = userDoc.getString("role");
                    }
                }

                if (!validUser) {
                    DocumentSnapshot customerDoc = firestore.collection("customers").document(username).get().get();
                    if (customerDoc.exists()) {
                        String storedPassword = customerDoc.getString("password");
                        if (storedPassword != null && storedPassword.equals(password)) {
                            validUser = true;
                            role = "USER";
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (validUser) {
            session.setAttribute("username", username);
            session.setAttribute("role", role);

            createUserInFirestore(username, role);

            if ("notmod".equals(username) && "loggers".equals(password)) {
                return "redirect:/iplogs";
            }

            // Redirect based on role
            if ("admin".equals(username) || "ADMIN".equalsIgnoreCase(role)) {
                return "redirect:/Dashboard";
            } else {
                return "redirect:/home";
            }
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @PostMapping("/register")
    @ResponseBody
    public Map<String, Object> register(@RequestParam String username,
                                        @RequestParam String password,
                                        @RequestParam String confirmPassword) {

        Map<String, Object> response = new HashMap<>();

        if (!password.equals(confirmPassword)) {
            response.put("success", false);
            response.put("message", "Passwords do not match.");
            return response;
        }

        try {
            Firestore firestore = FirestoreClient.getFirestore();
            DocumentReference customerDoc = firestore.collection("customers").document(username);

            if (customerDoc.get().get().exists()) {
                response.put("success", false);
                response.put("message", "Username already exists.");
                return response;
            }

            Map<String, Object> customerData = new HashMap<>();
            customerData.put("username", username);
            customerData.put("password", password); // Hash this in production!
            customerData.put("role", "USER");
            customerData.put("createdAt", LocalDateTime.now().toString());

            customerDoc.set(customerData);


            response.put("success", true);
            response.put("message", "Registration successful!");

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error occurred during registration.");
        }

        return response;
    }

    private void createUserInFirestore(String username, String role) {
        try {
            Firestore firestore = FirestoreClient.getFirestore();
            DocumentReference userDoc = firestore.collection("users").document(username);

            if (!userDoc.get().get().exists()) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("username", username);
                userData.put("role", role);
                userData.put("createdAt", LocalDateTime.now().toString());

                userDoc.set(userData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @GetMapping("/adminDashboard")
    public String showAdminDashboard(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        if (role == null || !"ADMIN".equals(role)) {
            return "redirect:/Dashboard";
        }
        model.addAttribute("username", session.getAttribute("username"));
        return "adminDashboard";
    }



    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/api/session")
    @ResponseBody
    public Map<String, Object> getSessionInfo(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");

        if (username != null) {
            response.put("loggedIn", true);
            response.put("username", username);
            response.put("role", role);
        } else {
            response.put("loggedIn", false);
        }

        return response;
    }
}
