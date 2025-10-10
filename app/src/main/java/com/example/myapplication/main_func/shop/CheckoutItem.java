package com.example.myapplication.main_func.shop;

public class CheckoutItem {
    private String id, name, img;
    private double price;           // discounted price
    private double originalPrice;   // only used if discount exists
    private boolean hasDiscount;
    private int quantity = 1;
    private boolean selected;

    public CheckoutItem(String id, String name, String img, double price, double originalPrice, boolean hasDiscount) {
        this.id = id;
        this.name = name;
        this.img = img;
        this.price = price;
        this.originalPrice = originalPrice;
        this.hasDiscount = hasDiscount;
        this.selected = true;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getImg() { return img; }
    public double getPrice() { return price; }
    public double getOriginalPrice() { return originalPrice; }
    public boolean hasDiscount() { return hasDiscount; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
}
