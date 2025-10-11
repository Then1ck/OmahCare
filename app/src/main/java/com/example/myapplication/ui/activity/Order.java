package com.example.myapplication.ui.activity;

public class Order {
    private String title;
    private String regNumber;
    private String status;
    private String imageUrl;
    private String category; // NEW

    public Order(String title, String regNumber, String status, String imageUrl, String category) {
        this.title = title;
        this.regNumber = regNumber;
        this.status = status;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public String getTitle() { return title; }
    public String getRegNumber() { return regNumber; }
    public String getStatus() { return status; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
}
