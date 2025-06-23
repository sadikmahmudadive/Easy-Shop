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

    // Default constructor required for calls to DataSnapshot.getValue(Product.class)
    public Product() {}

    // Full constructor for convenience (optional)
    public Product(String productId, String name, String brand, String category, String imageUrl, List<String> imageUrls,
                   String description, String price, String oldPrice, boolean isFavorite, Float avgRating, Integer reviewCount) {
        this.productId = productId;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.imageUrl = imageUrl;
        this.imageUrls = imageUrls;
        this.description = description;
        this.price = price;
        this.oldPrice = oldPrice;
        this.isFavorite = isFavorite;
        this.avgRating = avgRating;
        this.reviewCount = reviewCount;
    }

    // Getters and Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getName() { return name != null ? name : ""; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand != null ? brand : ""; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getCategory() { return category != null ? category : ""; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public String getDescription() { return description != null ? description : ""; }
    public void setDescription(String description) { this.description = description; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getOldPrice() { return oldPrice; }
    public void setOldPrice(String oldPrice) { this.oldPrice = oldPrice; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public Float getAvgRating() { return avgRating != null ? avgRating : 0f; }
    public void setAvgRating(Float avgRating) { this.avgRating = avgRating; }

    public Integer getReviewCount() { return reviewCount != null ? reviewCount : 0; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
}