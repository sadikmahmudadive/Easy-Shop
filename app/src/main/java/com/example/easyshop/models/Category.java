package com.example.easyshop.models;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private String name;
    private String imageUrl;

    public Category() { }

    public Category(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }

    public static List<Category> getDefaultCategories() {
        List<Category> list = new ArrayList<>();
        list.add(new Category("New", "url_or_drawable_here"));
        list.add(new Category("Clothes", "url_or_drawable_here"));
        list.add(new Category("Shoes", "url_or_drawable_here"));
        list.add(new Category("Accessories", "url_or_drawable_here"));
        return list;
    }
}