package com.example.myapplication.main_func.caregiver;

public class CaregiverModel {
    private String caregiverId; // ✅ new field for Firebase key
    private String name;
    private int exp;
    private double dist;
    private long cost;
    private String img;

    // Required empty constructor for Firebase
    public CaregiverModel() {}

    // Full constructor (optional, for manual creation)
    public CaregiverModel(String caregiverId, String name, int exp, double dist, long cost, String img) {
        this.caregiverId = caregiverId;
        this.name = name;
        this.exp = exp;
        this.dist = dist;
        this.cost = cost;
        this.img = img;
    }

    // ✅ Getters
    public String getCaregiverId() { return caregiverId; }
    public String getName() { return name; }
    public int getExp() { return exp; }
    public double getDist() { return dist; }
    public long getCost() { return cost; }
    public String getImg() { return img; }

    // ✅ Setters
    public void setCaregiverId(String caregiverId) { this.caregiverId = caregiverId; }
    public void setName(String name) { this.name = name; }
    public void setExp(int exp) { this.exp = exp; }
    public void setDist(double dist) { this.dist = dist; }
    public void setCost(long cost) { this.cost = cost; }
    public void setImg(String img) { this.img = img; }
}
