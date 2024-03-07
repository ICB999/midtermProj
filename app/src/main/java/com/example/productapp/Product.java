package com.example.productapp;

public class Product {
    private String id; // Unique product ID
    private String description;
    private String imgURL;
    private String name;
    private double price;
    private String userID;
    private String category;
    // Constructor
    public Product() {
        // Default constructor required for Firebase
    }

    // Getters and setters
    public String getId() {
        return id;
    }
    public String getCategory(){ return category;};
    public void setCategory(String category){this.category = category;};
    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
