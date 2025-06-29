package com.example.easyshop.models;

public class DeliveryMethod {
    public int id;
    public String name;
    public int iconRes;
    public String duration;
    public String eta;

    public DeliveryMethod(int id, String name, int iconRes, String duration) {
        this.id = id;
        this.name = name;
        this.iconRes = iconRes;
        this.duration = duration;
    }
}