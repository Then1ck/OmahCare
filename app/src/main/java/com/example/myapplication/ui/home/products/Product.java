package com.example.myapplication.ui.home.products;

public class Product {
    private String name, price, oldPrice, discount, imageUrl;
    private float rating;

    public Product(String name, String price, String oldPrice, String discount, String imageUrl, float rating) {
        this.name = name;
        this.price = price;
        this.oldPrice = oldPrice;
        this.discount = discount;
        this.imageUrl = imageUrl;
        this.rating = rating;
    }

    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getOldPrice() { return oldPrice; }
    public String getDiscount() { return discount; }
    public String getImageUrl() { return imageUrl; }
    public float getRating() { return rating; }
}
