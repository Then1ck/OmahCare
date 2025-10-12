package com.example.myapplication.misc;

public class PaymentMethod {
    private String name;
    private String imgUrl; // now a URL

    public PaymentMethod() {} // Required for Firebase

    public PaymentMethod(String name, String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
