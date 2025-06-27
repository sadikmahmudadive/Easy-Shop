package com.example.easyshop.models;

public class ShippingAddress {
    public int id;
    public String name;
    public String addressLine;
    public String city;

    public ShippingAddress(int id, String name, String addressLine, String city) {
        this.id = id;
        this.name = name;
        this.addressLine = addressLine;
        this.city = city;
    }
}