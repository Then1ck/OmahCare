package com.example.myapplication.ui.article.item;

public class Article {
    private String title;
    private String subtitle;
    private String author;
    private String date;

    public Article(String title, String subtitle, String author, String date) {
        this.title = title;
        this.subtitle = subtitle;
        this.author = author;
        this.date = date;
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getAuthor() { return author; }
    public String getDate() { return date; }
}
