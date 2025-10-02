package com.example.myapplication.ui.article.item;

public class Article {
    private String title;
    private String subtext;
    private String author;
    private String date;
    private String img;   // thumbnail
    private String pfp;   // profile picture

    public Article() {
        // Empty constructor required for Firebase
    }

    public Article(String title, String subtext, String author, String date, String img, String pfp) {
        this.title = title;
        this.subtext = subtext;
        this.author = author;
        this.date = date;
        this.img = img;
        this.pfp = pfp;
    }

    public String getTitle() { return title; }
    public String getSubtext() { return subtext; }
    public String getAuthor() { return author; }
    public String getDate() { return date; }
    public String getImg() { return img; }
    public String getPfp() { return pfp; }
}
