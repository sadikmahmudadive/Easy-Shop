package com.example.easyshop.models;

import java.io.Serializable;

public class CartItem implements Serializable {
    private Product product;
    private int quantity;

    public CartItem() {
        // Default constructor
    }

    public CartItem(Product product) {
        this.product = product;
        this.quantity = 1; // Default quantity is 1
    }

    // Getters
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }

    // Setters
    public void setProduct(Product product) { this.product = product; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}