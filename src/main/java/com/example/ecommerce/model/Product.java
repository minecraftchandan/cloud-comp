package com.example.ecommerce.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "new_products")
public class Product {

    @Id
    private String id; // MongoDB uses String/ObjectId for IDs

    private String name;
    private double price;
    private String pictureUrl;
    private String category;
    private String description;

    public Product() {}

    public Product(String name, double price, String pictureUrl, String category, String description) {
        this.name = name;
        this.price = price;
        this.pictureUrl = pictureUrl;
        this.category = category;
        this.description = description;
    }

    // Getters & Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
