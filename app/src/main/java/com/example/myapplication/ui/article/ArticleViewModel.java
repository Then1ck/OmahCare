package com.example.myapplication.ui.article;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.ui.article.item.Article;

import java.util.ArrayList;
import java.util.List;

public class ArticleViewModel extends ViewModel {

    private final MutableLiveData<List<Article>> articles;

    public ArticleViewModel() {
        articles = new MutableLiveData<>();

        List<Article> dummy = new ArrayList<>();
        dummy.add(new Article("Yoga asik bersama Yogsia (Yoga Lansia)",
                "Olahraga bersama lansia lainnya",
                "Averia Deize",
                "19 Jul 2025"));

        articles.setValue(dummy);
    }

    public LiveData<List<Article>> getArticles() {
        return articles;
    }
}
