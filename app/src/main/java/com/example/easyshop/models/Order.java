package com.example.easyshop.models;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String orderId;
    private String userId; // Added to store the user's ID
    private long orderDate;
    private double totalPrice;
    private List<CartItem> items;

    public Order() {
        // Default constructor is still needed for Firebase to deserialize objects
    }

    // Add this new constructor
    public Order(String orderId, String userId, List<CartItem> items, double totalPrice, long orderDate) {
        this.orderId = orderId;
        this.userId = userId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
    }

    // Getters
    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; } // Getter for userId
    public long getOrderDate() { return orderDate; }
    public double getTotalPrice() { return totalPrice; }
    public List<CartItem> getItems() { return items; }

    // Setters
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setUserId(String userId) { this.userId = userId; } // Setter for userId
    public void setOrderDate(long orderDate) { this.orderDate = orderDate; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setItems(List<CartItem> items) { this.items = items; }
}