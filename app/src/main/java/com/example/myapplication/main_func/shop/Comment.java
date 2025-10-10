package com.example.myapplication.main_func.shop;

public class Comment {
    private String actorName;
    private String content;
    private double rating;
    private String profileImg;

    public Comment() {}

    public Comment(String actorName, String content, double rating, String profileImg) {
        this.actorName = actorName;
        this.content = content;
        this.rating = rating;
        this.profileImg = profileImg;
    }

    public String getActorName() {
        return actorName;
    }

    public String getContent() {
        return content;
    }

    public double getRating() {
        return rating;
    }

    public String getProfileImg() {
        return profileImg;
    }
}
