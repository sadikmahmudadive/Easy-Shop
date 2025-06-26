package com.example.easyshop.models;

public class PromoCode {
    private String code;
    private String title;
    private String description;
    private int discountPercent;
    private int daysLeft;
    private String imageUrl;

    public PromoCode() {}

    public PromoCode(String code, String title, String description, int discountPercent, int daysLeft, String imageUrl) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.discountPercent = discountPercent;
        this.daysLeft = daysLeft;
        this.imageUrl = imageUrl;
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getDiscountPercent() { return discountPercent; }
    public int getDaysLeft() { return daysLeft; }
    public String getImageUrl() { return imageUrl; }

    public void setCode(String code) { this.code = code; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }
    public void setDaysLeft(int daysLeft) { this.daysLeft = daysLeft; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}