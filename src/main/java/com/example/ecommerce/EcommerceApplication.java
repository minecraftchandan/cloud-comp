package com.example.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.data.mongodb.repository.config.EnableMongoRepositories; // optional

@SpringBootApplication
// @EnableMongoRepositories("com.example.ecommerce.repository") // Optional if your repo is under this package and Spring Boot auto-scans
public class EcommerceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }
}


