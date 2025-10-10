package com.example.myapplication.main_func.shop;

public class PurchasedItem {
    public String id, name, img;
    public double price;
    public int quantity;

    public PurchasedItem() {} // Required for Firebase

    public PurchasedItem(CheckoutItem item) {
        this.id = item.getId();
        this.name = item.getName();
        this.img = item.getImg();
        this.price = item.getPrice();
        this.quantity = item.getQuantity();
    }
}
