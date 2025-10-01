package com.example.myapplication.ui.home.products;

public class Product {
    private String name, price, oldPrice, discount;
    private int imageResId;

    public Product(String name, String price, String oldPrice, String discount, int imageResId) {
        this.name = name;
        this.price = price;
        this.oldPrice = oldPrice;
        this.discount = discount;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getOldPrice() { return oldPrice; }
    public String getDiscount() { return discount; }
    public int getImageResId() { return imageResId; }
}
