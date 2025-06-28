package com.example.easyshop.models;

import android.util.Log;
import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private String productId;
    private String name;
    private String title;
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
    private String newPrice;      // Added for separate new price

    private float rating;
    private Double avgRating;
    private Integer reviewCount;
    private String discountText;

    // Bag/cart support
    private String selectedSize;
    private String color;
    private List<String> availableSizes;
    private String size;

    // For drafts
    private boolean draft;

    public Product() {}

    // Full constructor for easier creation
    public Product(String productId, String name, String brand, String description, String category,
                   String price, String oldPrice, String newPrice, String imageUrl, List<String> imageUrls,
                   boolean isFavorite, boolean favorite, boolean isNew, float rating, Double avgRating,
                   Integer reviewCount, String discountText, boolean draft) {
        this.productId = productId;
        this.name = name;
        this.title = name;
        this.brand = brand;
        this.description = description;
        this.category = category;
        this.price = price;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.imageUrl = imageUrl;
        this.imageUrls = imageUrls;
        this.isFavorite = isFavorite;
        this.favorite = favorite;
        this.isNew = isNew;
        this.rating = rating;
        this.avgRating = avgRating;
        this.reviewCount = reviewCount;
        this.discountText = discountText;
        this.draft = draft;
    }

    // --- Getters and Setters ---

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getName() { return name != null ? name : ""; }
    public void setName(String name) { this.name = name; }

    public String getTitle() { return title != null ? title : (name != null ? name : ""); }
    public void setTitle(String title) { this.title = title; }

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

    public boolean getIsFavorite() { return isFavorite; }
    public void setIsFavorite(boolean isFavorite) { this.isFavorite = isFavorite; }

    public boolean getFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }

    public boolean isNew() { return isNew; }
    public void setNew(boolean isNew) { this.isNew = isNew; }

    public String getOldPrice() { return oldPrice != null ? oldPrice : ""; }
    public void setOldPrice(String oldPrice) { this.oldPrice = oldPrice; }

    public String getPrice() { return price != null ? price : ""; }
    public void setPrice(String price) { this.price = price; }

    public String getNewPrice() { return newPrice != null ? newPrice : ""; }
    public void setNewPrice(String newPrice) { this.newPrice = newPrice; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public Double getAvgRating() { return avgRating; }
    public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public String getDiscountText() { return discountText; }
    public void setDiscountText(String discountText) { this.discountText = discountText; }

    public String getSelectedSize() { return selectedSize; }
    public void setSelectedSize(String selectedSize) { this.selectedSize = selectedSize; }

    public String getColor() { return color != null ? color : ""; }
    public void setColor(String color) { this.color = color; }

    public List<String> getAvailableSizes() { return availableSizes; }
    public void setAvailableSizes(List<String> availableSizes) { this.availableSizes = availableSizes; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public boolean isDraft() { return draft; }
    public void setDraft(boolean draft) { this.draft = draft; }

    // --- Debug helper ---
    public void logDebug(String tag) {
        Log.d(tag, "ProductId: " + productId
                + " | Name: " + name
                + " | Title: " + title
                + " | Brand: " + brand
                + " | Category: " + category
                + " | isFavorite: " + isFavorite
                + " | favorite: " + favorite
                + " | isNew: " + isNew
                + " | Price: " + price
                + " | OldPrice: " + oldPrice
                + " | NewPrice: " + newPrice
                + " | Rating: " + rating
                + " | AvgRating: " + avgRating
                + " | ReviewCount: " + reviewCount
                + " | DiscountText: " + discountText
                + " | SelectedSize: " + selectedSize
                + " | Color: " + color
                + " | Size: " + size
                + " | AvailableSizes: " + (availableSizes != null ? availableSizes.toString() : "null")
                + " | ImageUrl: " + imageUrl
                + " | Draft: " + draft);
        if (imageUrls != null) {
            Log.d(tag, "ImageUrls size: " + imageUrls.size());
        }
    }
}