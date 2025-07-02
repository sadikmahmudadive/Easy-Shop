package com.example.easyshop.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an order in the EasyShop app.
 * Includes user info, status, items, pricing, and logistics.
 */
public class Order implements Serializable {
    private String orderId;
    private String userId;
    private long orderDate;
    private double totalPrice;
    private List<CartItem> items;
    private String trackingNumber;
    private String status; // e.g. "Delivered", "Processing", "Cancelled"
    private String shippingAddress;
    private String paymentMethod;
    private String deliveryMethod;
    private String discountText;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private double shippingFee;
    private double discountValue;
    private String notes; // admin/customer notes

    public Order() {
        // Ensure items list is never null for Firebase deserialization
        this.items = new ArrayList<>();
    }

    public Order(String orderId, String userId, List<CartItem> items, double totalPrice, long orderDate,
                 String trackingNumber, String status, String shippingAddress,
                 String paymentMethod, String deliveryMethod, String discountText,
                 String customerName, String customerEmail, String customerPhone,
                 double shippingFee, double discountValue, String notes) {
        this.orderId = orderId;
        this.userId = userId;
        this.items = (items != null) ? items : new ArrayList<>();
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.trackingNumber = trackingNumber;
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.deliveryMethod = deliveryMethod;
        this.discountText = discountText;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.shippingFee = shippingFee;
        this.discountValue = discountValue;
        this.notes = notes;
    }

    // Getters
    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public long getOrderDate() { return orderDate; }
    public double getTotalPrice() { return totalPrice; }
    public List<CartItem> getItems() {
        return (items != null) ? items : new ArrayList<>();
    }
    public String getTrackingNumber() { return trackingNumber; }
    public String getStatus() { return status; }
    public String getShippingAddress() { return shippingAddress; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getDeliveryMethod() { return deliveryMethod; }
    public String getDiscountText() { return discountText; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getCustomerPhone() { return customerPhone; }
    public double getShippingFee() { return shippingFee; }
    public double getDiscountValue() { return discountValue; }
    public String getNotes() { return notes; }

    // Setters
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setOrderDate(long orderDate) { this.orderDate = orderDate; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setItems(List<CartItem> items) { this.items = (items != null) ? items : new ArrayList<>(); }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public void setStatus(String status) { this.status = status; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }
    public void setDiscountText(String discountText) { this.discountText = discountText; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public void setShippingFee(double shippingFee) { this.shippingFee = shippingFee; }
    public void setDiscountValue(double discountValue) { this.discountValue = discountValue; }
    public void setNotes(String notes) { this.notes = notes; }
}