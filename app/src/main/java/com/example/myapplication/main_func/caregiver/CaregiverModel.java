package com.example.myapplication.main_func.caregiver;

public class CaregiverModel {
    private String name;
    private String img;
    private double exp;
    private double dist;
    private double cost;

    // Required empty constructor for Firebase
    public CaregiverModel() { }

    public CaregiverModel(String name, String img, double exp, double dist, double cost) {
        this.name = name;
        this.img = img;
        this.exp = exp;
        this.dist = dist;
        this.cost = cost;
    }

    public String getName() { return name; }
    public String getImg() { return img; }
    public double getExp() { return exp; }
    public double getDist() { return dist; }
    public double getCost() { return cost; }

    public void setName(String name) { this.name = name; }
    public void setImg(String img) { this.img = img; }
    public void setExp(double exp) { this.exp = exp; }
    public void setDist(double dist) { this.dist = dist; }
    public void setCost(double cost) { this.cost = cost; }
}
