package com.example.easyshop.models;

import android.util.Log;
import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private String productId;
    private String name;
    private String brand;
    private String category;
    private String description;
    private String imageUrl;
    private List<String> imageUrls;
    private int imageResId;
    private boolean isFavorite;   // For "isFavorite" field in JSON
    private boolean favorite;     // For "favorite" field in JSON
    private boolean isNew;        // For "new" field in JSON
    private String oldPrice;
    private String price;
    private float rating;
    private Double avgRating;
    private Integer reviewCount;
    private String selectedSize;
    private String title;
    private String discountText;  // For "discountText" field (used in some sections)

    // New fields for cart/bag support
    private String color;
    private List<String> availableSizes;
    private String size;

    public Product() {}

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getName() { return name != null ? name : ""; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand != null ? brand : ""; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getCategory() { return category != null ? category : ""; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description != null ? description : ""; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    // For "isFavorite"
    public boolean getIsFavorite() { return isFavorite; }
    public void setIsFavorite(boolean isFavorite) { this.isFavorite = isFavorite; }

    // For "favorite"
    public boolean getFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }

    // For "new"
    public boolean isNew() { return isNew; }
    public void setNew(boolean isNew) { this.isNew = isNew; }

    public String getOldPrice() { return oldPrice; }
    public void setOldPrice(String oldPrice) { this.oldPrice = oldPrice; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    // For "avgRating" (nullable Double for null-checks)
    public Double getAvgRating() { return avgRating; }
    public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public String getSelectedSize() { return selectedSize; }
    public void setSelectedSize(String selectedSize) { this.selectedSize = selectedSize; }

    public String getTitle() { return title != null ? title : getName(); }
    public void setTitle(String title) { this.title = title; }

    public String getDiscountText() { return discountText; }
    public void setDiscountText(String discountText) { this.discountText = discountText; }

    // New for bag/cart
    public String getColor() { return color != null ? color : ""; }
    public void setColor(String color) { this.color = color; }

    public List<String> getAvailableSizes() { return availableSizes; }
    public void setAvailableSizes(List<String> availableSizes) { this.availableSizes = availableSizes; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    // --- Debug helper ---
    public void logDebug(String tag) {
        Log.d(tag, "ProductId: " + productId
                + " | Name: " + name
                + " | Brand: " + brand
                + " | Category: " + category
                + " | isFavorite: " + isFavorite
                + " | favorite: " + favorite
                + " | isNew: " + isNew
                + " | Price: " + price
                + " | OldPrice: " + oldPrice
                + " | Rating: " + rating
                + " | AvgRating: " + avgRating
                + " | ReviewCount: " + reviewCount
                + " | SelectedSize: " + selectedSize
                + " | Title: " + title
                + " | DiscountText: " + discountText
                + " | ImageUrl: " + imageUrl
                + " | Color: " + color
                + " | Size: " + size
                + " | AvailableSizes: " + (availableSizes != null ? availableSizes.toString() : "null"));
        if (imageUrls != null) {
            Log.d(tag, "ImageUrls size: " + imageUrls.size());
        }
    }
}