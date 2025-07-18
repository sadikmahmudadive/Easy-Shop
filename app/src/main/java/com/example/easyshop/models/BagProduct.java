package com.example.easyshop.models;

import java.io.Serializable;

public class BagProduct implements Serializable {
    private String productId;
    private String name;
    private String brand;
    private String description;
    private String category;
    private String color;
    private String size;
    private int quantity;
    private int price;
    private String imageUrl;

    public BagProduct() {}

    public BagProduct(String productId, String name, String brand, String description, String category,
                      String color, String size, int quantity, int price, String imageUrl) {
        this.productId = productId;
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.category = category;
        this.color = color;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}