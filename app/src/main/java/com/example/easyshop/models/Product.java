package com.example.easyshop.models;

import java.util.List;

public class Product {
    private String productId;
    private String name;
    private String brand;
    private String category;
    private String imageUrl;
    private List<String> imageUrls;
    private String description;
    private String price;
    private String oldPrice;
    private boolean isFavorite;

    // Average rating and review count for main rating
    private Float avgRating;
    private Integer reviewCount;

    public Product() {}

    // ... other getters/setters

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public String getOldPrice() { return oldPrice; }
    public void setOldPrice(String oldPrice) { this.oldPrice = oldPrice; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public Float getAvgRating() { return avgRating; }
    public void setAvgRating(Float avgRating) { this.avgRating = avgRating; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
}