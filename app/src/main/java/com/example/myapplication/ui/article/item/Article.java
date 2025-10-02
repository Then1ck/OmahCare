package com.example.myapplication.ui.article.item;

public class Article {
    private String title;
    private String subtext;
    private String author;
    private String date;
    private String img;   // thumbnail
    private String pfp;   // profile picture
    private String content; // full article content

    public Article() {
        // Empty constructor required for Firebase
    }

    public Article(String title, String subtext, String author, String date, String img, String pfp, String content) {
        this.title = title;
        this.subtext = subtext;
        this.author = author;
        this.date = date;
        this.img = img;
        this.pfp = pfp;
        this.content = content;
    }

    public String getTitle() { return title; }
    public String getSubtext() { return subtext; }
    public String getAuthor() { return author; }
    public String getDate() { return date; }
    public String getImg() { return img; }
    public String getPfp() { return pfp; }
    public String getContent() { return content; }
}
